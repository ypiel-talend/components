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

import java.io.IOException;
import java.util.List;

import org.apache.avro.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.component.runtime.SourceOrSink;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.mongodb.tmongodbclose.TMongoDBCloseProperties;
import org.talend.daikon.NamedThing;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.ValidationResultMutable;

import com.mongodb.Mongo;

/**
 * Close mongo instance
 */
public class MongoDBCloseSourceOrSink implements SourceOrSink {

    private transient static final Logger LOGGER = LoggerFactory.getLogger(MongoDBCloseSourceOrSink.class);

    public TMongoDBCloseProperties properties;

    @Override
    public ValidationResult initialize(RuntimeContainer container, ComponentProperties properties) {
        this.properties = (TMongoDBCloseProperties) properties;
        return ValidationResult.OK;
    }

    @Override
    public ValidationResult validate(RuntimeContainer container) {
        ValidationResultMutable result = new ValidationResultMutable();
        try {
            if (container != null) {
                Mongo mongo = (Mongo) container.getComponentData(properties.getReferencedComponentId(),
                        MongoDBSourceOrSink.KEY_MONGO);
                if (mongo != null) {
                    LOGGER.info("Closing the connection {}.", mongo.getServerAddressList());
                    mongo.close();
                    LOGGER.info("Connection closed.");
                }
            }
            result.setStatus(ValidationResult.Result.OK);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            result.setMessage(e.getMessage());
            result.setStatus(ValidationResult.Result.ERROR);
        }
        return result;
    }

    @Override
    public List<NamedThing> getSchemaNames(RuntimeContainer container) throws IOException {
        return null;
    }

    @Override
    public Schema getEndpointSchema(RuntimeContainer container, String schemaName) throws IOException {
        return null;
    }
}
