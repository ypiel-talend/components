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

package org.talend.components.mongodb.runtime.row;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.component.runtime.SourceOrSink;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.mongodb.runtime.MongoDBTestBasic;
import org.talend.components.mongodb.tmongodbrow.TMongoDBRowProperties;
import org.talend.daikon.properties.ValidationResult;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;

@Ignore
public class MongoDBRowSourceOrSinkTestIT extends MongoDBTestBasic {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDBRowSourceOrSinkTestIT.class);

    protected static final String DEFAULT_ROW_COMP_ID = "tMongoDBRow_1";

    @Override
    public void prepareTestData() {

    }

    /**
     * Test execute common function of mongodb
     */
    @Test
    public void testExecuteFunction() {
        String collection = "col_" + createRandom();
        String function = getDefaultInsertFunction(collection);
        try {
            MongoDBRowSourceOrSink sink = (MongoDBRowSourceOrSink) getInitedSourceOrSink(false, true);
            // 1.Insert data by execute function with parameters
            sink.properties.function.setValue(function);
            List<Object> params = new ArrayList<Object>();
            params.add("010024201709220001");
            params.add("John");
            params.add(25);
            params.add(1000);
            sink.properties.functionParams.parameter.setValue(params);
            sink.properties.dieOnError.setValue(true);
            ValidationResult result = sink.validate(getRuntimeContainer(DEFAULT_ROW_COMP_ID, true));
            assertEquals(ValidationResult.Result.OK, result.getStatus());
            // 2.Check data is insert
            assertThat(1.0, is(extractInsertedNumber(result.getMessage())));
        } finally {
            // 3.Drop collection
            dropCollectionIfExist(collection);
        }

    }

    /**
     * Test execute wrong function command
     */
    @Test
    public void testExecuteFunctionFailed() {
        MongoDBRowSourceOrSink sink = (MongoDBRowSourceOrSink) getInitedSourceOrSink(false, true);
        // 1.Execute a wrong function
        sink.properties.function.setValue("WRONG_COMMAND");
        sink.properties.dieOnError.setValue(true);
        ValidationResult result = sink.validate(getRuntimeContainer(DEFAULT_ROW_COMP_ID, true));
        assertEquals(ValidationResult.Result.ERROR, result.getStatus());

    }

    /**
     * Test execute key value command
     */
    @Test
    public void testKeyValueCommand() {
        String collection = "col_" + createRandom();
        try {
            // 1.Prepared 30 records.
            initCollection(collection, 30);

            // 2.Test execute command
            MongoDBRowSourceOrSink sink = (MongoDBRowSourceOrSink) getInitedSourceOrSink(false, true);
            sink.properties.executeCommand.setValue(true);
            sink.properties.executeKVCommand.setValue(true);
            sink.properties.dieOnError.setValue(true);
            List<String> keys = new ArrayList<String>();
            keys.add("count");
            keys.add("query");
            keys.add("hint");
            List<Object> values = new ArrayList<Object>();
            values.add(collection);
            values.add("{age:{$gt:25}}");
            values.add(1);
            sink.properties.keyValueTable.key.setValue(keys);
            sink.properties.keyValueTable.value.setValue(values);
            ValidationResult result = sink.validate(getRuntimeContainer(DEFAULT_ROW_COMP_ID, true));
            assertEquals(ValidationResult.Result.OK, result.getStatus());
        } finally {
            dropCollectionIfExist(collection);
        }

    }

    /**
     * Test execute wrong key value command
     */
    @Test
    public void testWrongKeyValueCommand() {
        MongoDBRowSourceOrSink sink = (MongoDBRowSourceOrSink) getInitedSourceOrSink(false, true);
        sink.properties.executeCommand.setValue(true);
        sink.properties.executeKVCommand.setValue(true);
        sink.properties.dieOnError.setValue(true);
        List<String> keys = new ArrayList<String>();
        keys.add("WRONG_COMMAND");
        keys.add("TEST");
        keys.add(null);
        List<Object> values = new ArrayList<Object>();
        values.add("TEMP_COLLECTION");
        sink.properties.keyValueTable.key.setValue(keys);
        sink.properties.keyValueTable.value.setValue(values);
        ValidationResult result = sink.validate(getRuntimeContainer(DEFAULT_ROW_COMP_ID, true));
        assertEquals(ValidationResult.Result.ERROR, result.getStatus());

    }

    /**
     * Test execute json command
     */
    @Test
    public void testExecJsonCommand() {
        String collection = "col_" + createRandom();
        MongoDBRowSourceOrSink sink = (MongoDBRowSourceOrSink) getInitedSourceOrSink(false, true);
        sink.properties.executeCommand.setValue(true);
        sink.properties.executeJSONCommand.setValue(true);
        sink.properties.dieOnError.setValue(true);
        // 1.Test create collection
        sink.properties.jsonCommand.setValue(" { create: \"" + collection + "\", capped: true, size: " + (64 * 1024) + " }");
        ValidationResult result = sink.validate(getRuntimeContainer(DEFAULT_ROW_COMP_ID, true));
        assertEquals(ValidationResult.Result.OK, result.getStatus());
        // 2.Test drop collection
        sink.properties.jsonCommand.setValue(" { drop:\"" + collection + "\"}");
        result = sink.validate(getRuntimeContainer(DEFAULT_ROW_COMP_ID, true));
        assertEquals(ValidationResult.Result.OK, result.getStatus());
    }

    /**
     * Test execute json command
     */
    @Test
    public void testWrongJsonCommand() {
        MongoDBRowSourceOrSink sink = (MongoDBRowSourceOrSink) getInitedSourceOrSink(false, true);
        sink.properties.executeCommand.setValue(true);
        sink.properties.executeJSONCommand.setValue(true);
        sink.properties.dieOnError.setValue(true);
        // 1.Test create collection
        sink.properties.jsonCommand.setValue("WRONG_JSON_COMMAND");
        ValidationResult result = sink.validate(getRuntimeContainer(DEFAULT_ROW_COMP_ID, true));
        assertEquals(ValidationResult.Result.ERROR, result.getStatus());
    }

    /**
     * Test execute common command
     */
    @Test
    public void testExecCommand() {
        MongoDBRowSourceOrSink sink = (MongoDBRowSourceOrSink) getInitedSourceOrSink(false, true);
        sink.properties.executeCommand.setValue(true);
        sink.properties.dieOnError.setValue(true);

        sink.properties.command.setValue("isMaster");
        ValidationResult result = sink.validate(getRuntimeContainer(DEFAULT_ROW_COMP_ID, true));
        assertEquals(ValidationResult.Result.OK, result.getStatus());
    }

    /**
     * Test execute wrong common command
     */
    @Test
    public void testWrongCommand() {
        MongoDBRowSourceOrSink sink = (MongoDBRowSourceOrSink) getInitedSourceOrSink(false, true);
        sink.properties.executeCommand.setValue(true);
        sink.properties.dieOnError.setValue(true);
        // 1.Test create collection
        sink.properties.command.setValue("WRONG_COMMON_COMMAND");
        ValidationResult result = sink.validate(getRuntimeContainer(DEFAULT_ROW_COMP_ID, true));
        assertEquals(ValidationResult.Result.ERROR, result.getStatus());
    }

    /**
     * Get initialized SourceOrSink instance
     * 
     * @param hasInput whether
     * @param clearGlobalMap
     * 
     * @return initialized SourceOrSink instance
     */
    protected SourceOrSink getInitedSourceOrSink(boolean hasInput, boolean clearGlobalMap) {
        SourceOrSink sourceOrSink = null;
        if (hasInput) {
            sourceOrSink = new MongoDBRowSink();
        } else {
            sourceOrSink = new MongoDBRowSourceOrSink();
        }
        TMongoDBRowProperties properties = new TMongoDBRowProperties("properties");
        properties.init();
        properties.connection = createConnectionProperties();
        properties.connection.database.setValue(DEFAULT_DB);
        RuntimeContainer container = getRuntimeContainer(CONNECTION_COMP_ID, clearGlobalMap);
        sourceOrSink.initialize(container, properties);
        return sourceOrSink;
    }

    public String getDefaultInsertFunction(String collection) {
        return "function(_id,name,age,score){ return db." + collection
                + ".insert({_id:_id,name:name,age:NumberInt(age),score:NumberInt(score)});}";
    }

    private Object getRetvalObject(String resultJson) {
        try {
            BasicDBObject basicObject = (BasicDBObject) JSON.parse(resultJson);
            return basicObject.get("retval");
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
            return null;
        }
    }

    /**
     * Wraps count to return a count of the number of documents in a collection or a view.
     * 
     * @param collection collection or view name
     * @return number of document
     */
    public Integer countDocuments(String collection) {
        MongoDBRowSourceOrSink sink = (MongoDBRowSourceOrSink) getInitedSourceOrSink(false, true);
        // 1.Insert data by execute function with parameters
        sink.properties.function.setValue("db." + collection + ".count()");

        sink.properties.dieOnError.setValue(true);
        ValidationResult result = sink.validate(getRuntimeContainer(DEFAULT_ROW_COMP_ID, true));
        assertEquals(ValidationResult.Result.OK, result.getStatus());
        return extractIntegerRetval(result.getMessage());
    }

    public boolean dropCollectionIfExist(String collection) {
        MongoDBRowSourceOrSink sink = (MongoDBRowSourceOrSink) getInitedSourceOrSink(false, true);
        // 1.Insert data by execute function with parameters
        sink.properties.function.setValue("db." + collection + ".drop()");

        sink.properties.dieOnError.setValue(true);
        ValidationResult result = sink.validate(getRuntimeContainer(DEFAULT_ROW_COMP_ID, true));
        assertEquals(ValidationResult.Result.OK, result.getStatus());
        return extractBooleanRetval(result.getMessage());
    }

    /**
     * Extract inserted count value {"retval" : { "nInserted" : 1.0 , "nUpserted" : 0.0 , "nMatched" : 0.0 , "nModified" : 0.0 ,
     * "nRemoved" : 0.0 ,...}}
     *
     * @param resultJson json string want to be extract
     * @return boolean value
     */
    public Double extractInsertedNumber(String resultJson) {
        Object retval = getRetvalObject(resultJson);
        assertTrue("Value is not BasicDBObject type", retval instanceof BasicDBObject);
        Object value = ((BasicDBObject) retval).get("nInserted");
        assertNotNull("Value of 'nInserted' is null", value);
        return (Double) value;
    }

    /**
     * Extract boolean retval value {"retval" : true }
     * 
     * @param resultJson json string want to be extract
     * @return boolean value
     */
    public Boolean extractBooleanRetval(String resultJson) {
        Object retval = getRetvalObject(resultJson);
        assertTrue("Value is not Boolean type", retval instanceof Boolean);
        return (Boolean) retval;
    }

    /**
     * Extract boolean retval value {"retval" : true }
     *
     * @param resultJson json string want to be extract
     * @return boolean value
     */
    public Integer extractIntegerRetval(String resultJson) {
        Object retval = getRetvalObject(resultJson);
        assertTrue("Value is not number type", retval instanceof Double);
        return ((Double) retval).intValue();
    }

    public void initCollection(String collection, int docNum) {
        String function = getDefaultInsertFunction(collection);
        MongoDBRowSink sink = (MongoDBRowSink) getInitedSourceOrSink(true, true);
        sink.getMongoDBRowProperties().function.setValue(function);
        sink.getMongoDBRowProperties().dieOnError.setValue(true);

        // 1.Begin part of component
        RuntimeContainer container = getRuntimeContainer(DEFAULT_ROW_COMP_ID, true);
        ValidationResult result = sink.validate(container);
        assertEquals(ValidationResult.Result.OK, result.getStatus());
        MongoDBRowWriteOperation writeOperation = (MongoDBRowWriteOperation) sink.createWriteOperation();
        MongoDBRowWriter writer = (MongoDBRowWriter) writeOperation.createWriter(container);
        int count = 0;
        try {
            writer.open(container.getCurrentComponentId());
            // 2.Main part of component
            for (int i = 0; i < docNum; i++) {
                List<Object> params = new ArrayList<Object>();
                params.add("" + (100000 + i));
                params.add("John");
                params.add(i);
                params.add(1000 + i);
                // This simulate get parameters from input connector
                sink.getMongoDBRowProperties().functionParams.parameter.setValue(params);
                writer.write(null);
            }
            // 3.End part of component
            writer.close();
            // 4.Check whether expected number of documents were inserted
            count = countDocuments(collection);
            assertThat(docNum, is(count));
        } catch (IOException e) {
            count = countDocuments(collection);
            fail("Unexpected exception: " + e.getMessage());
        }

    }

}