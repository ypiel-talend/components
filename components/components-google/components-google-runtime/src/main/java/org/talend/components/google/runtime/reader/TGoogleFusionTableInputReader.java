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
package org.talend.components.google.runtime.reader;

import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.avro.generic.IndexedRecord;
import org.talend.components.api.component.runtime.AbstractBoundedReader;
import org.talend.components.api.component.runtime.BoundedReader;
import org.talend.components.api.component.runtime.BoundedSource;

/**
 * Reader for Google Fusion Tables. Reads rows of specified table
 */
public class TGoogleFusionTableInputReader extends AbstractBoundedReader<IndexedRecord> implements BoundedReader<IndexedRecord> {

    /**
     * Constructor sets {@link Source} of this {@link Reader}
     * 
     * @param source {@link Source} of this {@link Reader}
     */
    protected TGoogleFusionTableInputReader(BoundedSource source) {
        super(source);
    }

    @Override
    public boolean start() throws IOException {
        return false;
    }

    @Override
    public boolean advance() throws IOException {
        return false;
    }

    @Override
    public IndexedRecord getCurrent() throws NoSuchElementException {
        return null;
    }

    @Override
    public Map<String, Object> getReturnValues() {
        return null;
    }

}
