package org.talend.components.mongodb.runtime;


import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.mongodb.RuntimeContainerMock;
import org.talend.components.mongodb.MongoDBConnectionProperties.AuthenticationMechanism;
import org.talend.components.mongodb.tmongodbbulkload.TMongoDBBulkLoadProperties;
import org.talend.components.mongodb.tmongodbbulkload.TMongoDBBulkLoadProperties.DataAction;
import org.talend.components.mongodb.tmongodbbulkload.TMongoDBBulkLoadProperties.FileType;
import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.avro.SchemaConstants;
import org.talend.daikon.i18n.GlobalI18N;
import org.talend.daikon.i18n.I18nMessages;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.ValidationResult.Result;

public class MongoDBBulkLoadRuntimeTest {

    File file;

    MongoDBBulkLoadRuntime bulkLoadRuntime;

    TMongoDBBulkLoadProperties bulkLoadProperties = new TMongoDBBulkLoadProperties("test");

    protected static final I18nMessages MESSAGES = GlobalI18N.getI18nMessageProvider()
            .getI18nMessages(MongoDBBulkLoadRuntime.class);

    String dbHome = "C:\\Program Files\\MongoDB\\Server\\3.4";

    RuntimeContainer runtimeContainer;

    @Before
    public void beforeClass() throws IOException {
        bulkLoadProperties.init();
        bulkLoadProperties.setupProperties();

        bulkLoadRuntime = new MongoDBBulkLoadRuntime();

        file = File.createTempFile("MongoDbTest", "json");
        file.deleteOnExit();
        // BufferedWriter br = new BufferedWriter(new java.io.FileWriter(file));
        // br.write("");

        runtimeContainer = new RuntimeContainerMock();

    }

    @Test
    public void testInitialize() throws IOException {
        ValidationResult vr1 = bulkLoadRuntime.initialize(runtimeContainer, bulkLoadProperties);
        Assert.assertEquals(MESSAGES.getMessage("error.vacantMongoDBHome"), vr1.getMessage());

        bulkLoadProperties.mongoDBHome.setValue(dbHome);
        ValidationResult vr2 = bulkLoadRuntime.initialize(runtimeContainer, bulkLoadProperties);
        Assert.assertEquals(MESSAGES.getMessage("error.vacantDataFile"), vr2.getMessage());

        bulkLoadProperties.dataFile.setValue(file.getAbsolutePath());
        ValidationResult vr3 = bulkLoadRuntime.initialize(runtimeContainer, bulkLoadProperties);
        Assert.assertEquals(Result.OK, vr3.getStatus());

    }

    @Test
    public void testPrepareCommend() {
        bulkLoadProperties.mongoDBHome.setValue(dbHome);
        bulkLoadProperties.dataFile.setValue(file.getAbsolutePath());
        bulkLoadRuntime.initialize(runtimeContainer, bulkLoadProperties);
        bulkLoadProperties.connection.host.setValue("localhost");
        bulkLoadProperties.connection.database.setValue("test");
        bulkLoadProperties.collection.collectionName.setValue("collection");
        bulkLoadProperties.connection.requiredAuthentication.setValue(true);
        bulkLoadProperties.connection.userPassword.userId.setValue("admin");
        bulkLoadProperties.connection.userPassword.password.setValue("admin");
        bulkLoadProperties.connection.setAuthenticationDatabase.setValue(true);
        bulkLoadProperties.connection.authenticationMechanism.setValue(AuthenticationMechanism.MONGODBCR_MEC);
        String[] result1 = new String[0];
        result1 = bulkLoadRuntime.prepareCommend();
        Assert.assertEquals(19, result1.length);
        Assert.assertEquals("localhost", result1[2]);
        Assert.assertEquals("test", result1[6]);
        Assert.assertEquals("collection", result1[8]);
        Assert.assertTrue(result1[10].equals(result1[12]));
        Assert.assertEquals(file.getAbsolutePath(), result1[18]);

        bulkLoadProperties.connection.authenticationMechanism.setValue(AuthenticationMechanism.KERBEROS_MEC);
        bulkLoadProperties.fileType.setValue(FileType.json);
        bulkLoadProperties.jsonArray.setValue(true);
        result1 = bulkLoadRuntime.prepareCommend();
        Assert.assertEquals("GSSAPI", result1[10]);
        Assert.assertEquals("$external", result1[18]);
        Assert.assertEquals("json", result1[20]);
        Assert.assertEquals(24, result1.length);

    }
    
    @Test
    public void testPrepareCommend2() {
        bulkLoadProperties.mongoDBHome.setValue(dbHome);
        bulkLoadProperties.dataFile.setValue(file.getAbsolutePath());
        bulkLoadRuntime.initialize(runtimeContainer, bulkLoadProperties);
        bulkLoadProperties.connection.useReplicaSet.setValue(true);
        
        bulkLoadProperties.connection.replicaSetTable.host.setValue(Arrays.asList(new String[]{"localhost","192.168.0.1"}));
        bulkLoadProperties.connection.replicaSetTable.port.setValue(Arrays.asList(new Integer[]{12345,8888}));
        
        bulkLoadProperties.collection.collectionName.setValue("collection");
        bulkLoadProperties.connection.requiredAuthentication.setValue(true);
        bulkLoadProperties.connection.setAuthenticationDatabase.setValue(true);
        bulkLoadProperties.connection.authenticationMechanism.setValue(AuthenticationMechanism.PLAIN_MEC);
        
        bulkLoadProperties.headerLine.setValue(true);
        bulkLoadProperties.ignoreBlanks.setValue(true);
        String[] result1 = new String[0];
        result1 = bulkLoadRuntime.prepareCommend();
        Assert.assertEquals("localhost:12345,192.168.0.1:8888", result1[2]);
        Assert.assertEquals("--headerline", result1[20]);
        Assert.assertEquals(21, result1.length);

        bulkLoadProperties.connection.authenticationMechanism.setValue(AuthenticationMechanism.SCRAMSHA1_MEC);
        bulkLoadProperties.dataAction.setValue(DataAction.UPSERT);
        bulkLoadProperties.upsertField.columnName.setValue(Arrays.asList(new String[]{"_id","name"}));
        bulkLoadProperties.fileType.setValue(FileType.json);
        bulkLoadProperties.additionalArgs.argument.setValue(Arrays.asList(new String[]{"--dog","--cat"}));
        bulkLoadProperties.additionalArgs.value.setValue(Arrays.asList(new Object[]{"bark","miaow"}));
        bulkLoadProperties.useLocalDBPath.setValue(true);
        
        result1 = bulkLoadRuntime.prepareCommend();
        Assert.assertEquals("--upsert", result1[17]);
        Assert.assertEquals(24, result1.length);


    }
    
    @Test
    public void testGetDBColumn(){
        bulkLoadProperties.mongoDBHome.setValue(dbHome);
        bulkLoadProperties.dataFile.setValue(file.getAbsolutePath());
        Schema s = SchemaBuilder.record("Main").fields().name("_id").prop(SchemaConstants.TALEND_COLUMN_DB_LENGTH, "300")// $NON-NLS-3$
                .prop(SchemaConstants.TALEND_IS_LOCKED, "true").prop(SchemaConstants.TALEND_COLUMN_DB_COLUMN_NAME, "_id")//$NON-NLS-1$
                .type(AvroUtils._string()).noDefault().endRecord();
        bulkLoadProperties.collection.main.schema.setValue(s);
        bulkLoadRuntime.initialize(runtimeContainer, bulkLoadProperties);
        String result = bulkLoadRuntime.getDBColumn();
        Assert.assertEquals("_id", result);
    }


    @Test
    public void testThreadRun() throws IOException, InterruptedException {
        bulkLoadRuntime.initialize(runtimeContainer, bulkLoadProperties);
        int status = bulkLoadRuntime.ThreadRun(new String[]{"java","-version"});
        Assert.assertEquals(0, status);
    }
    
    @Ignore
    @Test
    public void testRunatDriver(){
        bulkLoadProperties.mongoDBHome.setValue(dbHome);
        bulkLoadProperties.dataFile.setValue(file.getAbsolutePath());
        bulkLoadRuntime.initialize(runtimeContainer, bulkLoadProperties);
        bulkLoadProperties.connection.host.setValue("localhost");
        bulkLoadProperties.connection.database.setValue("test");
        bulkLoadProperties.collection.collectionName.setValue("collection");
        bulkLoadProperties.connection.requiredAuthentication.setValue(true);
        bulkLoadProperties.connection.userPassword.userId.setValue("admin");
        bulkLoadProperties.connection.userPassword.password.setValue("admin");
        bulkLoadProperties.connection.setAuthenticationDatabase.setValue(true);
        bulkLoadProperties.connection.authenticationDatabase.setValue("fff");
        bulkLoadProperties.connection.authenticationMechanism.setValue(AuthenticationMechanism.MONGODBCR_MEC);
        bulkLoadProperties.printLog.setValue(true);
        bulkLoadProperties.headerLine.setValue(true);
        bulkLoadProperties.ignoreBlanks.setValue(true);
        bulkLoadRuntime.runAtDriver(runtimeContainer);
        //no Excception means success
    }

}
