package org.talend.components.mongodb.runtime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.SSLSocketFactory;

import org.apache.avro.Schema;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.mongodb.MongoDBConnectionProperties;
import org.talend.components.mongodb.MongoDBProvideConnectionProperties;
import org.talend.components.mongodb.MongoDBRuntimeSourceOrSink;
import org.talend.components.mongodb.avro.MongoDBSchemaInferrer;
import org.talend.daikon.NamedThing;
import org.talend.daikon.SimpleNamedThing;
import org.talend.daikon.i18n.GlobalI18N;
import org.talend.daikon.i18n.I18nMessages;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.ValidationResultMutable;
import org.talend.daikon.properties.property.Property;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

public class MongoDBSourceOrSink implements MongoDBRuntimeSourceOrSink {

    protected static final I18nMessages MESSAGES = GlobalI18N.getI18nMessageProvider().getI18nMessages(MongoDBSourceOrSink.class);

    private transient static final Logger LOGGER = LoggerFactory.getLogger(MongoDBSourceOrSink.class);

    // The max number of retrieved records which used to guess schema of the collection
    public static final int COUNT_ROWS = 50;

    public static final String KEY_MONGO = "mongo";

    public static final String KEY_DB = "db";

    private static final String DB_COLLECTION_MARK = "$";

    public MongoDBProvideConnectionProperties properties;

    @Override
    public ValidationResult initialize(RuntimeContainer container, ComponentProperties properties) {
        this.properties = (MongoDBProvideConnectionProperties) properties;
        return ValidationResult.OK;
    }

    protected Mongo connect(RuntimeContainer container) throws IOException {

        MongoDBConnectionProperties props = properties.getConnectionProperties();
        Mongo mongo = null;

        String refComponentId = props.getReferencedComponentId();
        // Using another component's connection
        if (refComponentId != null) {
            // In a runtime container
            if (container != null) {
                mongo = (Mongo) container.getComponentData(refComponentId, KEY_MONGO);
                if (mongo != null) {
                    return mongo;
                }
                throw new IOException(MESSAGES.getMessage("error.refComponentNotConnected", refComponentId));
            }
            // Design time
            props = props.getReferencedConnectionProperties();
        }

        MongoClientOptions clientOptions = null;
        if (props.useSSL.getValue()) {
            clientOptions = new MongoClientOptions.Builder().socketFactory(SSLSocketFactory.getDefault()).build();
        } else {
            clientOptions = new MongoClientOptions.Builder().build();
        }
        List<MongoCredential> mongoCredentialList = getCredential(props);

        List<ServerAddress> serverAddressList = getServerAddressList(props);
        if (props.useReplicaSet.getValue()) {
            if (serverAddressList.size() < 1) {
                throw new IOException(MESSAGES.getMessage("error.replicaSet.setting"));
            }
            mongo = new MongoClient(serverAddressList, mongoCredentialList, clientOptions);
        } else {
            mongo = new MongoClient(serverAddressList.get(0), mongoCredentialList, clientOptions);
        }

        LOGGER.info("Connecting to {}", mongo.getServerAddressList());
        // Check the connection
        mongo.getAddress();

        if (container != null) {
            container.setComponentData(container.getCurrentComponentId(), KEY_MONGO, mongo);
            if (!StringUtils.isEmpty(props.database.getValue())) {
                DB db = mongo.getDB(props.database.getValue());
                container.setComponentData(container.getCurrentComponentId(), KEY_DB, db);
            } else {
                throw new IOException(MESSAGES.getMessage("error.db.missing"));
            }
        } else {
            // Check whether have right to list databases when database is not specified
            if (StringUtils.isEmpty(props.database.getValue())) {
                mongo.getDatabaseNames();
            }
        }

        return mongo;
    }

    /**
     * Get collection names list from current database
     * 
     * @param container runtime container
     * 
     * @return collection names list
     * @throws IOException connection failed
     */
    @Override
    public List<NamedThing> getSchemaNames(RuntimeContainer container) throws IOException {
        try {
            DB db = getDatabase(container);
            List<NamedThing> returnList = new ArrayList<>();
            Set<String> collections = db.getCollectionNames();
            for (String collectionName : collections) {
                returnList.add(new SimpleNamedThing(collectionName, collectionName));
            }
            return returnList;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    /**
     * Get all database names from current connection
     * 
     * @param container runtime container
     * 
     * @return the database names list
     * @throws IOException connection failed
     */
    @Override
    public List<NamedThing> getDatabaseNames(RuntimeContainer container) throws IOException {
        // Returns the list with a database names
        List<NamedThing> returnList = new ArrayList<>();
        try {
            Mongo mongo = connect(container);
            List<String> dbNames = mongo.getDatabaseNames();
            // Fetch all database
            LOGGER.debug("Exist databases:");
            for (String dbName : dbNames) {
                returnList.add(new SimpleNamedThing(dbName, dbName));
                LOGGER.debug(dbName);
            }
            return returnList;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    /**
     * Get the collection name which include prefix with db name
     *
     * @param container runtime container
     * @param databases selected databases
     * 
     * @return collection names list
     * @throws IOException connection failed
     */
    @Override
    public List<NamedThing> getCollectionNames(RuntimeContainer container, List<NamedThing> databases) throws IOException {
        List<NamedThing> returnList = new ArrayList<>();
        try {
            Mongo mongo = connect(container);
            for (NamedThing dbName : databases) {
                Set<String> collectionNames = mongo.getDB(dbName.getName()).getCollectionNames();
                for (String collectionName : collectionNames) {
                    String collectionNameWithDB = dbName + DB_COLLECTION_MARK + collectionName;
                    returnList.add(new SimpleNamedThing(collectionNameWithDB, collectionNameWithDB));
                }
            }
        } catch (Exception e) {
            throw new IOException(e);
        }
        return returnList;
    }

    /**
     * Get db and collection mapping from the selected collection names
     *
     * @param selectedCollectionNames The select collection name which are saved like this: [db name]$[collection name]
     *
     * @return The da and collection mapping which key is the database name, and the value is the selected collection in the
     * database
     */
    @Override
    public Map<String, List<String>> getDBCollectionMapping(Property<List<NamedThing>> selectedCollectionNames) {
        Map<String, List<String>> dbCollectionMapping = new HashMap<>();
        List<NamedThing> collectionNames = selectedCollectionNames.getValue();
        for (NamedThing collectionName : collectionNames) {
            String collectionNameWithDB = collectionName.getName();
            String[] names = collectionNameWithDB.split("\\" + DB_COLLECTION_MARK);
            List<String> collections = dbCollectionMapping.get(names[0]);
            if (collections != null) {
                if (!collections.contains(names[1])) {
                    dbCollectionMapping.get(names[0]).add(names[1]);
                }
            } else {
                collections = new ArrayList<>();
                collections.add(names[1]);
                dbCollectionMapping.put(names[0], collections);
            }
        }
        return dbCollectionMapping;
    }

    /**
     * Get schema of the collection
     * 
     * @param container runtime container information
     * @param collectionName the name of the collection
     *
     * @return the schema guess from the collection
     *
     * @throws IOException when connection failed
     */
    @Override
    public Schema getEndpointSchema(RuntimeContainer container, String collectionName) throws IOException {
        try {
            DB db = getDatabase(container);
            DBCollection collection = db.getCollection(collectionName);
            return MongoDBSchemaInferrer.get().inferSchema(collection);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    /**
     * Validate the connection
     * 
     * @param container runtime container information
     * 
     * @return validate result
     */
    @Override
    public ValidationResult validate(RuntimeContainer container) {
        ValidationResultMutable vr = new ValidationResultMutable();
        try {
            connect(container);
        } catch (Exception e) {
            vr.setMessage(e.getMessage());
            vr.setStatus(ValidationResult.Result.ERROR);
            return vr;
        }
        vr.setStatus(ValidationResult.Result.OK);
        vr.setMessage(MESSAGES.getMessage("connection.success"));
        return vr;
    }

    /**
     * Get the database instance based on setting of database of connection properties
     * 
     * @param container runtime container
     *
     * @return DB instance
     * 
     * @throws Exception specified db is not be shared
     */

    private DB getDatabase(RuntimeContainer container) throws Exception {
        Mongo mongo = connect(container);
        DB db = null;
        if (container != null) {
            db = (DB) container.getComponentData(container.getCurrentComponentId(), KEY_DB);
            if (db == null) {
                throw new IOException(MESSAGES.getMessage("error.db.unshared", container.getCurrentComponentId()));
            }
        } else {
            db = mongo.getDB(properties.getConnectionProperties().database.getValue());
        }
        return db;
    }

    /**
     * Get credential list from the properties
     * 
     * @param properties the connection properties
     * 
     * @return credential list if required authentication is required
     */
    protected List<MongoCredential> getCredential(MongoDBConnectionProperties properties) throws IOException {
        List<MongoCredential> mongoCredentialList = new ArrayList<MongoCredential>();
        if (properties.requiredAuthentication.getValue()) {
            MongoCredential mongoCredential = null;
            if ((MongoDBConnectionProperties.AuthenticationMechanism.NEGOTIATE_MEC
                    .equals(properties.authenticationMechanism.getValue()))
                    || (MongoDBConnectionProperties.AuthenticationMechanism.PLAIN_MEC
                            .equals(properties.authenticationMechanism.getValue()))
                    || (MongoDBConnectionProperties.AuthenticationMechanism.SCRAMSHA1_MEC
                            .equals(properties.authenticationMechanism.getValue()))
                    || (MongoDBConnectionProperties.AuthenticationMechanism.MONGODBCR_MEC
                            .equals(properties.authenticationMechanism.getValue()))) {
                String userId = properties.userPassword.userId.getValue();
                String password = properties.userPassword.password.getValue();
                if (userId == null || password == null) {
                    throw new IllegalArgumentException(MESSAGES.getMessage("error.usernamepwd.missing"));
                }
                String authDBName = null;
                if (properties.setAuthenticationDatabase.getValue()) {
                    authDBName = properties.authenticationDatabase.getValue();
                } else {
                    authDBName = properties.database.getValue();
                }
                if ((MongoDBConnectionProperties.AuthenticationMechanism.NEGOTIATE_MEC
                        .equals(properties.authenticationMechanism.getValue()))) {
                    if (MongoDBConnectionProperties.DBVersion.MONGODB_3_0_X.equals(properties.dbVersion.getValue())
                            || MongoDBConnectionProperties.DBVersion.MONGODB_3_2_X.equals(properties.dbVersion.getValue())) {
                        mongoCredential = MongoCredential.createCredential(userId, authDBName, password.toCharArray());
                    } else {
                        mongoCredential = MongoCredential.createMongoCRCredential(userId, authDBName, password.toCharArray());
                    }
                } else if (MongoDBConnectionProperties.AuthenticationMechanism.PLAIN_MEC
                        .equals(properties.authenticationMechanism.getValue())) {
                    mongoCredential = MongoCredential.createPlainCredential(userId, "$external", password.toCharArray());
                } else if (MongoDBConnectionProperties.AuthenticationMechanism.SCRAMSHA1_MEC
                        .equals(properties.authenticationMechanism.getValue())) {
                    mongoCredential = MongoCredential.createScramSha1Credential(userId, authDBName, password.toCharArray());
                } else if (MongoDBConnectionProperties.AuthenticationMechanism.MONGODBCR_MEC
                        .equals(properties.authenticationMechanism.getValue())) {
                    mongoCredential = MongoCredential.createMongoCRCredential(userId, authDBName, password.toCharArray());
                }
            } else {
                // TODO need to recheck whether we can do like this in new framework.
                System.setProperty("java.security.krb5.realm", properties.kerberos.realm.getValue());
                System.setProperty("java.security.krb5.kdc", properties.kerberos.kdcServer.getValue());
                System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");
                mongoCredential = MongoCredential.createGSSAPICredential(properties.kerberos.userPrincipal.getValue());
            }
            mongoCredentialList.add(mongoCredential);
        }
        return mongoCredentialList;

    }

    protected List<ServerAddress> getServerAddressList(MongoDBConnectionProperties properties) throws IOException {
        List<ServerAddress> serverAddressList = new java.util.ArrayList<ServerAddress>();
        if (properties.useReplicaSet.getValue()) {
            Object replicatSetHosts = properties.replicaSetTable.host.getValue();
            Object replicatSetPorts = properties.replicaSetTable.port.getValue();
            if (replicatSetHosts != null && replicatSetPorts != null && (replicatSetHosts instanceof List)
                    && (replicatSetPorts instanceof List)) {
                for (int i = 0; i < ((List) replicatSetHosts).size(); i++) {
                    serverAddressList.add(new ServerAddress(((List<String>) replicatSetHosts).get(i),
                            ((List<Integer>) replicatSetPorts).get(i)));
                }
            } else {
                throw new IOException(MESSAGES.getMessage("error.replicaSet.setting"));
            }
        } else {
            serverAddressList.add(new ServerAddress(properties.host.getValue(), properties.port.getValue()));
        }
        return serverAddressList;
    }

}
