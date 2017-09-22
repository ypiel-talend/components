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

import org.talend.components.api.component.runtime.Sink;
import org.talend.components.api.component.runtime.WriteOperation;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.mongodb.runtime.MongoDBSourceOrSink;
import org.talend.components.mongodb.tmongodbrow.TMongoDBRowProperties;

import com.mongodb.DB;
import com.mongodb.Mongo;

public class MongoDBRowSink extends MongoDBSourceOrSink implements Sink {

    private static final long serialVersionUID = 1L;

    @Override
    public WriteOperation<?> createWriteOperation() {
        return new MongoDBRowWriteOperation(this);
    }

    public TMongoDBRowProperties getMongoDBRowProperties() {
        return (TMongoDBRowProperties) properties;
    }

    public DB getDB(RuntimeContainer container) throws IOException {
        String refComponentId = properties.getConnectionProperties().getReferencedComponentId();
        DB db = null;
        if (container != null) {
            if (refComponentId != null) {
                db = (DB) container.getComponentData(refComponentId, KEY_DB);
                if (db != null) {
                    return db;
                }
                throw new IOException(MESSAGES.getMessage("error.refComponentNotConnected", refComponentId));
            } else {
                db = (DB) container.getComponentData(container.getCurrentComponentId(), KEY_DB);
            }
        }
        return db;
    }

    public Mongo getMongo(RuntimeContainer container) throws IOException {
        String refComponentId = properties.getConnectionProperties().getReferencedComponentId();
        Mongo mongo = null;
        if (container != null) {
            if (refComponentId != null) {
                mongo = (Mongo) container.getComponentData(refComponentId, KEY_MONGO);
                if (mongo != null) {
                    return mongo;
                }
                throw new IOException(MESSAGES.getMessage("error.refComponentNotConnected", refComponentId));
            } else {
                mongo = (Mongo) container.getComponentData(container.getCurrentComponentId(), KEY_MONGO);
            }
        }
        return mongo;
    }
}
