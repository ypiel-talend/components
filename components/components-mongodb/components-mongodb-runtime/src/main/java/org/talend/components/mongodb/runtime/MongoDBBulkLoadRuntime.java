package org.talend.components.mongodb.runtime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.component.runtime.ComponentDriverInitialization;
import org.talend.components.api.component.runtime.RuntimableRuntime;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.exception.ComponentException;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.mongodb.MongoDBConnectionProperties.AuthenticationMechanism;
import org.talend.components.mongodb.tmongodbbulkload.TMongoDBBulkLoadDefinition;
import org.talend.components.mongodb.tmongodbbulkload.TMongoDBBulkLoadProperties;
import org.talend.components.mongodb.tmongodbbulkload.TMongoDBBulkLoadProperties.DataAction;
import org.talend.daikon.avro.SchemaConstants;
import org.talend.daikon.i18n.GlobalI18N;
import org.talend.daikon.i18n.I18nMessages;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.ValidationResultMutable;

public class MongoDBBulkLoadRuntime
        implements ComponentDriverInitialization<ComponentProperties>, RuntimableRuntime<ComponentProperties> {

    private static final long serialVersionUID = 829731606958765194L;
    
    RuntimeContainer runtimeContainer;
    
    String componentId;

    String errorMessage = "";

    Long NB_LINE = 0L;

    TMongoDBBulkLoadProperties bulkProperties;

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDBBulkLoadRuntime.class);

    protected static final I18nMessages MESSAGES = GlobalI18N.getI18nMessageProvider()
            .getI18nMessages(MongoDBBulkLoadRuntime.class);

    @Override
    public ValidationResult initialize(RuntimeContainer runtimeContainer, ComponentProperties properties) {
        this.bulkProperties = (TMongoDBBulkLoadProperties) properties;
        this.runtimeContainer = runtimeContainer;
        componentId = runtimeContainer.getCurrentComponentId();
        ValidationResultMutable vr = new ValidationResultMutable();
        vr.setStatus(ValidationResult.Result.ERROR);
        if (MongoDBRuntimeHelper.isVacant(bulkProperties.mongoDBHome.getValue())) {
            vr.setMessage(MESSAGES.getMessage("error.vacantMongoDBHome"));

            return vr;
        }
        if (MongoDBRuntimeHelper.isVacant(bulkProperties.dataFile.getValue())) {
            vr.setMessage(MESSAGES.getMessage("error.vacantDataFile"));
            return vr;
        }

        return ValidationResult.OK;

    }

    @Override
    public void runAtDriver(RuntimeContainer container) {

        try {
            ThreadRun(prepareCommend());
        } catch (Exception e) {

            LOGGER.error(e.getLocalizedMessage());
            throw new ComponentException(e);

        }

        setReturnValues();

    }

    protected String[] prepareCommend() {
        List<String> args = new ArrayList<String>();
        args.add(bulkProperties.mongoDBHome.getValue() + "/bin/mongoimport");

        if (bulkProperties.useLocalDBPath.getValue()) {
            args.add("--dbpath");
            args.add(bulkProperties.localDBPath.getValue());
        } else {
            if (!bulkProperties.connection.useReplicaSet.getValue()) {
                args.add("--host");
                args.add(bulkProperties.connection.host.getValue());
                args.add("--port");
                args.add("" + bulkProperties.connection.port.getValue());

            } else {
                StringBuffer repHosts = new StringBuffer();
                if (bulkProperties.specifyReplicateSet.getValue()) {
                    repHosts.append(bulkProperties.replicateName.getValue() + "/");
                }
                List<String> hosts = bulkProperties.connection.replicaSetTable.host.getValue();
                if (hosts != null && !hosts.isEmpty()) {
                    List<Integer> values = bulkProperties.connection.replicaSetTable.port.getValue();
                    for (int i = 0; i < hosts.size(); i++) {
                        if (!MongoDBRuntimeHelper.isVacant(hosts.get(i))) {
                            repHosts.append(hosts.get(i));
                        }
                        if (!MongoDBRuntimeHelper.isVacant(String.valueOf(values.get(i)))) {
                            repHosts.append(':').append(String.valueOf(values.get(i))).append(',');
                        }
                    }

                }
                if (repHosts.length() > 0) {
                    repHosts.deleteCharAt(repHosts.length() - 1);
                }
                args.add("--host");
                args.add(repHosts.toString());

            }
            if (bulkProperties.connection.useSSL.getValue()) {
                args.add("--ssl");
            }

        }
        args.add("--db");
        args.add(bulkProperties.connection.database.getValue());
        args.add("--collection");
        args.add(bulkProperties.collection.collectionName.getValue());

        boolean useAuthenticationDatabase = bulkProperties.connection.setAuthenticationDatabase.getValue();
        if (bulkProperties.connection.requiredAuthentication.getValue()) {
            if (bulkProperties.connection.authenticationMechanism.getValue().equals(AuthenticationMechanism.KERBEROS_MEC)) {
                // GSSAPI SASL (KERBEROS)
                // SSL is not compatible with Kerberos in mongoimport
                if (!bulkProperties.connection.useSSL.getValue()) {
                    args.add("--authenticationMechanism");
                    args.add("GSSAPI");
                    args.add("--gssapiServiceName");
                    args.add(bulkProperties.connection.kerberos.realm.getValue());
                    args.add("--gssapiHostName");
                    args.add(bulkProperties.connection.kerberos.kdcServer.getValue());
                    args.add("--username");
                    args.add(bulkProperties.connection.kerberos.userPrincipal.getValue());
                    args.add("--authenticationDatabase");
                    args.add(bulkProperties.krbAuthDatabase.getValue());

                }
            } else {
                args.add("--username");
                args.add(bulkProperties.connection.userPassword.userId.getValue());
                args.add("--password");
                args.add(bulkProperties.connection.userPassword.password.getValue());
                String authenticationDatabase = bulkProperties.connection.authenticationDatabase.getValue();
                if (bulkProperties.connection.authenticationMechanism.getValue().equals(AuthenticationMechanism.SCRAMSHA1_MEC)) {
                    args.add("--authenticationMechanism");
                    args.add("SCRAM-SHA-1");
                    if (useAuthenticationDatabase && !MongoDBRuntimeHelper.isVacant(authenticationDatabase)) {
                        args.add("--authenticationDatabase");
                        args.add(authenticationDatabase);
                    }
                    // X509 can't work today without adding a property to locate the x509 certificate
                } else if (bulkProperties.connection.authenticationMechanism.getValue()
                        .equals(AuthenticationMechanism.MONGODBCR_MEC)) {
                    args.add("--authenticationMechanism");
                    args.add("MONGODB-CR");
                    if (useAuthenticationDatabase && !MongoDBRuntimeHelper.isVacant(authenticationDatabase)) {
                        args.add("--authenticationDatabase");
                        args.add(authenticationDatabase);
                    }
                } else if (bulkProperties.connection.authenticationMechanism.getValue()
                        .equals(AuthenticationMechanism.PLAIN_MEC)) {
                    args.add("--authenticationMechanism");
                    args.add("PLAIN");
                    args.add("--authenticationDatabase");
                    args.add("'$external'");
                }

            }

        }

        String fileType = bulkProperties.fileType.getValue().toString();

        args.add("--type");
        args.add(fileType);

        if (!MongoDBRuntimeHelper.isVacant(bulkProperties.dataFile.getValue())) {
            args.add("--file");
            args.add(bulkProperties.dataFile.getValue());
        }

        if (bulkProperties.dropExistCollection.getValue()) {
            args.add("--drop");
        }
        if (bulkProperties.dataAction.getValue().equals(DataAction.UPSERT)) {
            List<String> upsertFieldList = bulkProperties.upsertField.columnName.getValue();
            StringBuilder upsertFields = new StringBuilder();
            if (upsertFieldList != null && !upsertFieldList.isEmpty()) {
                for (String field : upsertFieldList) {
                    upsertFields.append(field).append(',');
                }
                upsertFields.deleteCharAt(upsertFields.length() - 1);

            }
            args.add("--upsert");

            args.add("--upsertFields");
            args.add(upsertFields.toString());

        }

        if (!"JSON".equalsIgnoreCase(fileType)) {
            if (bulkProperties.ignoreBlanks.getValue()) {
                args.add("--ignoreBlanks");
            }
            String fileds = getDBColumn();
            if (bulkProperties.headerLine.getValue()) {
                args.add("--headerline");
            } else if (!"".equals(fileds)) {
                args.add("--fields");
                args.add(fileds);
            }
        } else {
            if (bulkProperties.jsonArray.getValue()) {
                args.add("--jsonArray");
            }
        }

        List<String> argument = bulkProperties.additionalArgs.argument.getValue();
        if (argument != null && !argument.isEmpty()) {
            List<Object> values = bulkProperties.additionalArgs.value.getValue();
            for (int i = 0; i < argument.size(); i++) {
                if (!MongoDBRuntimeHelper.isVacant(argument.get(i))) {
                    args.add(argument.get(i));
                }
                if (!MongoDBRuntimeHelper.isVacant(String.valueOf(values.get(i)))) {
                    args.add(String.valueOf(values.get(i)));
                }
            }

        }

        StringBuilder sb = new StringBuilder();
        for (String s : args) {
            sb.append(s + " ");
        }
        LOGGER.info(" Execute command " + sb.toString() + ".");
        if (bulkProperties.connection.useReplicaSet.getValue()) {
            LOGGER.info(componentId + " - Start to import the data");
        } else {
            LOGGER.info(componentId + " - Start to import the data into [" + bulkProperties.connection.host.getValue() + ":"
                    + bulkProperties.connection.port.getValue() + "/" + bulkProperties.connection.database.getValue() + "]");
        }

        return args.toArray(new String[0]);

    }

    protected String getDBColumn() {
        Schema schema = bulkProperties.collection.main.schema.getValue();
        List<Field> fields = schema.getFields();
        StringBuffer sb = new StringBuffer();
        for (Field field : fields) {
            sb.append(field.getProp(SchemaConstants.TALEND_COLUMN_DB_COLUMN_NAME)).append(',');
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    protected int ThreadRun(String[] args) throws IOException, InterruptedException {

        ProcessBuilder runtime = new ProcessBuilder(args);

        runtime.redirectErrorStream(true);

        final Process process = runtime.start();
        
        Thread normal = new Thread() {
            @Override
            public void run() {
                try {
                    java.io.BufferedReader reader = new java.io.BufferedReader(
                            new java.io.InputStreamReader(process.getInputStream()));
                    String line = "";
                    try {
                        while ((line = reader.readLine()) != null) {
                            if(bulkProperties.printLog.getValue()){
                                System.out.println(line);
                            }
                            
                            int im = line.indexOf("imported");
                            int obj = line.indexOf("object");
                            int doc = line.indexOf("document");
                            if (im > 0 && obj > 0) {
                                line = line.substring(im + 8, obj).trim();
                            } else if (im > 0 && doc > 0) {
                                line = line.substring(im + 8, doc).trim();
                            }
                            if (line.matches("^[\\d]+$")) {
                                NB_LINE = Long.parseLong(line);
                            }
                        }
                    } finally {
                        reader.close();
                    }
                } catch (java.io.IOException ioe) {
                    ioe.printStackTrace();
                    LOGGER.error(componentId + " - " + ioe.getLocalizedMessage());
                }
            }
        };
        normal.start();

        if (bulkProperties.printLog.getValue()) {
            printLog(process);
        }
        int status = process.waitFor();
        normal.interrupt();
        return status;

    }

    protected void printLog(final Process process) {
        Thread error = new Thread() {
            @Override
            public void run() {
                try {
                    java.io.BufferedReader reader = new java.io.BufferedReader(
                            new java.io.InputStreamReader(process.getErrorStream()));
                    StringBuffer messages = new StringBuffer();
                    String line = "";
                    try {
                        while ((line = reader.readLine()) != null) {
                            System.err.println(line);
                            messages.append(line);
                        }
                    } finally {
                        reader.close();
                        errorMessage = messages.toString();
                    }
                } catch (java.io.IOException ioe) {
                    ioe.printStackTrace();
                    LOGGER.error(componentId + " - " + ioe.getLocalizedMessage());
                }
            }
        };
        error.start();
    }

    private void setReturnValues() {
        String componentId = runtimeContainer.getCurrentComponentId();

        runtimeContainer.setComponentData(componentId, TMongoDBBulkLoadDefinition.RETURN_ERROR_MESSAGE, errorMessage);
        runtimeContainer.setComponentData(componentId, TMongoDBBulkLoadDefinition.RETURN_TOTAL_RECORD_COUNT, NB_LINE);
    }

}
