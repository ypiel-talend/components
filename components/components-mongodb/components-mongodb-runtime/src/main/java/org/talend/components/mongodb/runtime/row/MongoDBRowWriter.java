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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.component.runtime.Result;
import org.talend.components.api.component.runtime.WriteOperation;
import org.talend.components.api.component.runtime.Writer;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.mongodb.common.FunctionParametersTable;
import org.talend.components.mongodb.common.KeyValueTable;
import org.talend.components.mongodb.tmongodbrow.TMongoDBRowProperties;
import org.talend.daikon.i18n.GlobalI18N;
import org.talend.daikon.i18n.I18nMessages;

import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.util.JSON;

public class MongoDBRowWriter implements Writer {

    protected static final I18nMessages MESSAGES = GlobalI18N.getI18nMessageProvider().getI18nMessages(MongoDBRowWriter.class);

    private transient static final Logger LOGGER = LoggerFactory.getLogger(MongoDBRowWriter.class);

    private MongoDBRowSink sink;

    private RuntimeContainer container;

    private WriteOperation writeOperation;

    private TMongoDBRowProperties properties;

    private Mongo mongo;

    private DB db;

    private Result result;

    private CommandResult cmdResult;

    public MongoDBRowWriter(WriteOperation writeOperation, RuntimeContainer container) {
        this.writeOperation = writeOperation;
        this.container = container;
        sink = (MongoDBRowSink) writeOperation.getSink();
        properties = sink.getMongoDBRowProperties();
        result = new Result();
    }

    @Override
    public void open(String uId) throws IOException {
        mongo = sink.getMongo(container);
        db = sink.getDB(container);
    }

    @Override
    public void write(Object object) throws IOException {
        try {
            if (properties.executeCommand.getValue()) {
                if (properties.executeKVCommand.getValue()) {
                    DBObject dbObj = getKVCommandObject(properties.keyValueTable);
                    LOGGER.info("Execute key value command: {}.", dbObj);
                    cmdResult = db.command(dbObj);
                } else if (properties.executeJSONCommand.getValue()) {
                    LOGGER.info("Execute JSON command: {}", properties.jsonCommand.getValue());
                    cmdResult = db.command((BasicDBObject) JSON.parse(properties.jsonCommand.getValue()));
                } else {
                    LOGGER.info("Execute command: {}", properties.command.getValue());
                    cmdResult = db.command(properties.command.getValue());
                }
            } else {
                LOGGER.info("Execute script function: {}", properties.function.getValue());
                Object[] args = getFunctionArgs(properties.functionParams);
                LOGGER.info("Arguments: {}", Arrays.asList(args));
                cmdResult = db.doEval(properties.function.getValue(), args);
            }
            if (cmdResult.ok()) {
                LOGGER.info("Return the result is: {}", cmdResult);
            } else {
                if (properties.dieOnError.getValue()) {
                    throw new IOException(MESSAGES.getMessage("error.execute.command", cmdResult));
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            if (properties.dieOnError.getValue()) {
                throw new IOException(e);
            }
        }
    }

    @Override
    public Result close() throws IOException {
        if (properties.connection.getReferencedComponentId() == null) {
            if (mongo != null) {
                LOGGER.info("Closing the connection {}.", mongo.getServerAddressList());
                mongo.close();
                LOGGER.info("Connection closed.");
            }
        }
        return result;
    }

    @Override
    public WriteOperation getWriteOperation() {
        return writeOperation;
    }

    /**
     * Get command {@link DBObject} object for key value command
     * 
     * @param keyValueTable key value table configuration
     * @return instance of {@link DBObject}
     * @throws IOException wrong key value configuration
     */
    public DBObject getKVCommandObject(KeyValueTable keyValueTable) throws IOException {
        DBObject dbObj = new BasicDBObject();
        Object keys = keyValueTable.key.getValue();
        Object values = keyValueTable.value.getValue();
        if (keys != null && values != null && keys instanceof List && values instanceof List) {
            List<String> keyList = (List<String>) keys;
            List<String> valueList = (List<String>) values;
            for (int i = 0; i < keyList.size(); i++) {
                String key = keyList.get(i);
                if (key == null) {
                    throw new IOException(MESSAGES.getMessage("error.command.key"));
                }
                if (i < valueList.size()) {
                    dbObj.put(key, valueList.get(i));
                } else {
                    dbObj.put(key, null);
                }
            }
        }
        return dbObj;
    }

    /**
     * Get params array from the setting
     * 
     * @param params
     * @return
     */
    public Object[] getFunctionArgs(FunctionParametersTable params) {
        Object keys = params.parameter.getValue();
        Object[] args = new Object[0];
        if (keys != null && keys instanceof List) {
            args = ((List<Object>) keys).toArray();
        }
        return args;
    }

    public CommandResult getCmdResult() {
        return cmdResult;
    }

}
