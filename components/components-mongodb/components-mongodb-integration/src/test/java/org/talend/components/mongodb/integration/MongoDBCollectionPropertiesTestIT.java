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

package org.talend.components.mongodb.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.talend.components.mongodb.common.MongoDBDefinition.SOURCE_OR_SINK_CLASS;
import static org.talend.components.mongodb.common.MongoDBDefinition.USE_CURRENT_JVM_PROPS;
import static org.talend.components.mongodb.common.MongoDBDefinition.getSandboxedInstance;
import static org.talend.components.mongodb.runtime.MongoDBSourceOrSink.KEY_MONGO;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.mongodb.MongoDBConnectionProperties;
import org.talend.components.mongodb.MongoDBRuntimeSourceOrSink;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.sandbox.SandboxedInstance;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.WriteResult;

@Ignore
public class MongoDBCollectionPropertiesTestIT extends MongoDBTestBasic {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDBCollectionPropertiesTestIT.class);

    private static final String CONNECTION_COMP_ID = "tMongoDBConnection_1";

    private static final String TEST_DB_NAME = "admin";

    private Mongo mongo;

    @Before
    public void prepareTestData() {
        try (SandboxedInstance sandboxedInstance = getSandboxedInstance(SOURCE_OR_SINK_CLASS, USE_CURRENT_JVM_PROPS)) {
            MongoDBRuntimeSourceOrSink ss = (MongoDBRuntimeSourceOrSink) sandboxedInstance.getInstance();
            RuntimeContainer container = getRuntimeContainer(CONNECTION_COMP_ID);
            MongoDBConnectionProperties properties = createConnectionProperties();
            ss.initialize(container, properties);
            ValidationResult result = ss.validate(container);
            LOGGER.info(properties.host.getValue());
            LOGGER.info(String.valueOf(properties.port.getValue()));
            LOGGER.info(properties.userPassword.userId.getValue());
            LOGGER.info(properties.userPassword.password.getValue());
            LOGGER.info(result.getMessage());
            assertEquals(ValidationResult.Result.OK, result.getStatus());
            mongo = (Mongo) container.getComponentData(CONNECTION_COMP_ID, KEY_MONGO);
            assertNotNull(mongo);
            DB db = mongo.getDB(TEST_DB_NAME);
            assertNotNull(db);
            DBCollection collection = db.getCollection("people");
            assertNotNull(collection);
            BasicDBObject initData = new BasicDBObject();
            initData.put("id", "0000000001");
            initData.put("name", "name1");
            initData.put("age", 18);
            WriteResult writeResult = collection.insert(initData);
            LOGGER.info(writeResult.toString());
        }
    }

    @Ignore
    @Test
    public void testListAllDatabase() throws Throwable {
        LOGGER.info(mongo.getDatabaseNames().toString());
    }

    @Test
    public void testSimpleQuery() throws Throwable {
        DB db = mongo.getDB("testdb");
        assertNotNull(db);
        DBCollection collection = db.getCollection("test");
        assertNotNull(collection);
        DBCursor cursor = collection.find();
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            LOGGER.info(String.valueOf(obj.get("id")));
            LOGGER.info(String.valueOf(obj.get("name")));
            LOGGER.info(String.valueOf(obj.get("age")));
        }
    }

}
