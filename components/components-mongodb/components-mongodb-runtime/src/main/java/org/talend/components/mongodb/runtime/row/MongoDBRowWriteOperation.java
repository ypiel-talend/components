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

import java.util.Map;

import org.talend.components.api.component.runtime.Sink;
import org.talend.components.api.component.runtime.WriteOperation;
import org.talend.components.api.component.runtime.Writer;
import org.talend.components.api.container.RuntimeContainer;

/**
 * MongoDB row write operation
 */
public class MongoDBRowWriteOperation implements WriteOperation {

    private static final long serialVersionUID = 1L;

    private Sink sink;

    public MongoDBRowWriteOperation(Sink sink) {
        this.sink = sink;
    }

    @Override
    public void initialize(RuntimeContainer adaptor) {
        // nothing to do here
    }

    @Override
    public Map<String, Object> finalize(Iterable iterable, RuntimeContainer container) {
        // TODO nothing to return ?
        return null;
    }

    @Override
    public Writer createWriter(RuntimeContainer container) {
        return new MongoDBRowWriter(this, container);
    }

    @Override
    public Sink getSink() {
        return sink;
    }
}
