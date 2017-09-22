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
import java.util.List;

import org.apache.avro.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.component.runtime.SourceOrSink;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.mongodb.tmongodbrow.TMongoDBRowProperties;
import org.talend.daikon.NamedThing;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.ValidationResultMutable;

import com.mongodb.CommandResult;

/**
 * For mongodb row which have no input
 */
public class MongoDBRowSourceOrSink implements SourceOrSink {

    private transient static final Logger LOGGER = LoggerFactory.getLogger(MongoDBRowSourceOrSink.class);

    protected TMongoDBRowProperties properties;

    private RuntimeContainer container;

    @Override
    public ValidationResult initialize(RuntimeContainer container, ComponentProperties properties) {
        this.container = container;
        this.properties = (TMongoDBRowProperties) properties;
        return ValidationResult.OK;
    }

    @Override
    public ValidationResult validate(RuntimeContainer container) {
        MongoDBRowSink sink = new MongoDBRowSink();
        sink.initialize(container, properties);
        ValidationResult result = sink.validate(container);
        if (!ValidationResult.Result.OK.equals(result.getStatus())) {
            return result;
        }
        MongoDBRowWriteOperation writeOperation = (MongoDBRowWriteOperation) sink.createWriteOperation();
        MongoDBRowWriter writer = (MongoDBRowWriter) writeOperation.createWriter(container);
        ValidationResultMutable vr = new ValidationResultMutable();
        CommandResult cmdResult = null;
        try {
            writer.open(container.getCurrentComponentId());
            writer.write(null);
            cmdResult = writer.getCmdResult();
            writer.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            vr.setMessage(e.getMessage());
            vr.setStatus(ValidationResult.Result.ERROR);
            return vr;
        }
        if (cmdResult != null) {
            vr.setMessage(cmdResult.toString());
        }
        vr.setStatus(ValidationResult.Result.OK);
        return vr;
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
