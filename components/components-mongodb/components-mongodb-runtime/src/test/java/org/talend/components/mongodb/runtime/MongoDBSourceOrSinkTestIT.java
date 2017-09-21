// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.components.mongodb.runtime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.talend.daikon.properties.property.PropertyFactory.newProperty;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.reflect.TypeLiteral;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.mongodb.MongoDBConnectionProperties;
import org.talend.daikon.NamedThing;
import org.talend.daikon.SimpleNamedThing;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.property.Property;

import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

public class MongoDBSourceOrSinkTestIT extends MongoDBTestBasic {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDBSourceOrSinkTestIT.class);

    private static final String CONNECTION_COMP_ID = "tMongoDBConnection_1";

    private static final String DEFAULT_DB = "testdb";

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Before
    @Override
    public void prepareTestData() {
        // Nothing to do
    }

    @Ignore("Test environment is unavailable")
    @Test
    public void testInitialize() throws Exception {
        MongoDBSourceOrSink sourceOrSink = new MongoDBSourceOrSink();
        assertNull(sourceOrSink.properties);
        MongoDBConnectionProperties properties = createConnectionProperties();
        RuntimeContainer container = getRuntimeContainer(CONNECTION_COMP_ID);
        sourceOrSink.initialize(container, properties);
        assertNotNull(sourceOrSink.properties);

    }

    /**
     * If active the auth and not specify the database,
     * then would get error authorized command "listDatabases"
     */
    @Ignore("Test environment is unavailable")
    @Test
    public void testValidate() {
        MongoDBSourceOrSink sourceOrSink = getInitializedSourceOrSink();

        ValidationResult result = sourceOrSink.validate(null);
        assertEquals(ValidationResult.Result.ERROR, result.getStatus());
        LOGGER.debug(result.getMessage());
        assertTrue(result.getMessage().contains("not authorized"));
    }

    /**
     * When runtime container is not empty
     * if the database is not specified, throw error notice
     */
    @Ignore("Test environment is unavailable")
    @Test
    public void testValidateWithRuntimeContainer() {
        MongoDBSourceOrSink sourceOrSink = getInitializedSourceOrSink();
        // Database name missing
        ValidationResult resultError = sourceOrSink.validate(getRuntimeContainer(CONNECTION_COMP_ID));
        LOGGER.debug(resultError.getMessage());
        assertEquals(ValidationResult.Result.ERROR, resultError.getStatus());

        // Specify the database name
        sourceOrSink.properties.database.setValue(DEFAULT_DB);
        ValidationResult resultOK = sourceOrSink.validate(getRuntimeContainer(CONNECTION_COMP_ID));
        LOGGER.debug(resultOK.getMessage());
        assertEquals(ValidationResult.Result.OK, resultOK.getStatus());

    }

    @Ignore("Current test environment can't get expect result")
    @Test
    public void testValidateWrongPWD() throws Exception {
        MongoDBSourceOrSink sourceOrSink = getInitializedSourceOrSink();
        sourceOrSink.properties.requiredAuthentication.setValue(true);
        sourceOrSink.properties.userPassword.password.setValue("WRONG_PWD");
        ValidationResult result = sourceOrSink.validate(null);
        LOGGER.debug(result.getMessage());
        assertEquals(ValidationResult.Result.ERROR, result.getStatus());
    }

    @Test
    public void testGetCredential() throws IOException {
        MongoDBSourceOrSink sourceOrSink = getInitializedSourceOrSink();
        sourceOrSink.properties.requiredAuthentication.setValue(true);
        List<MongoCredential> credentialList = sourceOrSink.getCredential(sourceOrSink.properties);
        assertEquals(1, credentialList.size());
        assertNull(credentialList.get(0).getMechanism());

        sourceOrSink.properties.dbVersion.setValue(MongoDBConnectionProperties.DBVersion.MONGODB_2_5_X);
        credentialList = sourceOrSink.getCredential(sourceOrSink.properties);
        assertEquals(1, credentialList.size());
        assertEquals("MONGODB-CR", credentialList.get(0).getMechanism());

        sourceOrSink.properties.authenticationMechanism.setValue(MongoDBConnectionProperties.AuthenticationMechanism.PLAIN_MEC);
        credentialList = sourceOrSink.getCredential(sourceOrSink.properties);
        assertEquals(1, credentialList.size());
        assertEquals("PLAIN", credentialList.get(0).getMechanism());

        sourceOrSink.properties.authenticationMechanism
                .setValue(MongoDBConnectionProperties.AuthenticationMechanism.KERBEROS_MEC);
        credentialList = sourceOrSink.getCredential(sourceOrSink.properties);
        assertEquals(1, credentialList.size());
        assertEquals("GSSAPI", credentialList.get(0).getMechanism());

        sourceOrSink.properties.authenticationMechanism
                .setValue(MongoDBConnectionProperties.AuthenticationMechanism.SCRAMSHA1_MEC);
        credentialList = sourceOrSink.getCredential(sourceOrSink.properties);
        assertEquals(1, credentialList.size());
        assertEquals("SCRAM-SHA-1", credentialList.get(0).getMechanism());

        sourceOrSink.properties.userPassword.password.setValue(null);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Username or password missing");
        credentialList = sourceOrSink.getCredential(sourceOrSink.properties);
    }

    @Ignore
    @Test
    public void testGetSchemaNames() throws Exception {
        // Need to implement
    }

    @Ignore
    @Test
    public void testGetDatabaseNames() throws Exception {
        // Need to implement
    }

    @Ignore
    @Test
    public void testGetCollectionNames() throws Exception {
        // Need to implement
    }

    @Test
    public void testGetDBCollectionMapping() throws Exception {
        Property<List<NamedThing>> selectedCollectionNames = newProperty(new TypeLiteral<List<NamedThing>>() {
        }, "selectedCollectionNames"); //$NON-NLS-1$
        NamedThing coll_1 = new SimpleNamedThing("test_1$collection_1", "test_1$collection_1");
        NamedThing coll_2 = new SimpleNamedThing("test_1$collection_2", "test_1$collection_2");
        NamedThing coll_3 = new SimpleNamedThing("test_2$collection_3", "test_2$collection_3");
        NamedThing coll_4 = new SimpleNamedThing("test_3$collection_4", "test_3$collection_4");
        NamedThing coll_5 = new SimpleNamedThing("test_3$collection_5", "test_3$collection_5");
        NamedThing coll_6 = new SimpleNamedThing("test_3$collection_6", "test_3$collection_6");
        NamedThing coll_7 = new SimpleNamedThing("test_3$collection_7", "test_3$collection_7");
        selectedCollectionNames.setValue(Arrays.asList(coll_1, coll_2, coll_3, coll_4, coll_5, coll_6, coll_7));
        MongoDBSourceOrSink sourceOrSink = new MongoDBSourceOrSink();
        Map<String, List<String>> dbCollectionMapping = sourceOrSink.getDBCollectionMapping(selectedCollectionNames);
        assertTrue(dbCollectionMapping.keySet().containsAll(Arrays.asList("test_1", "test_1", "test_3")));
        assertTrue(dbCollectionMapping.get("test_1").containsAll(Arrays.asList("collection_1", "collection_2")));
        assertTrue(dbCollectionMapping.get("test_2").containsAll(Arrays.asList("collection_3")));
        assertTrue(dbCollectionMapping.get("test_3")
                .containsAll(Arrays.asList("collection_4", "collection_5", "collection_6", "collection_7")));

    }

    @Test
    public void testGetServerAddressList() throws IOException {
        MongoDBSourceOrSink sourceOrSink = getInitializedSourceOrSink();

        // Use replicaSet unchecked
        List<ServerAddress> addressList = sourceOrSink.getServerAddressList(sourceOrSink.properties);
        assertEquals(1, addressList.size());

        // Use replicaSet checked
        sourceOrSink.properties.useReplicaSet.setValue(true);
        try {
            addressList = sourceOrSink.getServerAddressList(sourceOrSink.properties);
            fail("Expect get exception: \"java.io.IOException: The replicaSet table should not be empty\"");
        } catch (IOException e) {
        }
        // Init replicaSet table
        sourceOrSink.properties.replicaSetTable.host.setValue(Arrays.asList("localhost", null, "localhost"));
        sourceOrSink.properties.replicaSetTable.port.setValue(Arrays.asList(27017, 27018, 27019));

        addressList = sourceOrSink.getServerAddressList(sourceOrSink.properties);
        assertEquals(3, addressList.size());

        assertEquals("localhost", addressList.get(0).getHost());
        // Default host 127.0.0.1
        assertEquals("127.0.0.1", addressList.get(1).getHost());
        assertEquals("localhost", addressList.get(2).getHost());

        assertEquals(27017, addressList.get(0).getPort());
        assertEquals(27018, addressList.get(1).getPort());
        assertEquals(27019, addressList.get(2).getPort());

    }

    @Ignore
    @Test
    public void getEndpointSchema() throws Exception {
        // Need to implement
    }

    protected MongoDBSourceOrSink getInitializedSourceOrSink() {
        MongoDBSourceOrSink sourceOrSink = new MongoDBSourceOrSink();
        MongoDBConnectionProperties properties = createConnectionProperties();
        RuntimeContainer container = getRuntimeContainer(CONNECTION_COMP_ID);
        sourceOrSink.initialize(container, properties);
        return sourceOrSink;
    }

}