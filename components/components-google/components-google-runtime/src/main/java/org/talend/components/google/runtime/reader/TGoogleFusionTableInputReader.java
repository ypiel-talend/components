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
import java.security.GeneralSecurityException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.avro.generic.IndexedRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.component.runtime.AbstractBoundedReader;
import org.talend.components.api.component.runtime.BoundedReader;
import org.talend.components.api.component.runtime.Reader;
import org.talend.components.api.component.runtime.Source;
import org.talend.components.google.avro.RowConverter;

import com.google.api.services.fusiontables.Fusiontables;
import com.google.api.services.fusiontables.Fusiontables.Query.Sql;
import com.google.api.services.fusiontables.model.Sqlresponse;

/**
 * Reader for Google Fusion Tables. Reads rows of specified table
 */
public class TGoogleFusionTableInputReader extends AbstractBoundedReader<IndexedRecord> implements BoundedReader<IndexedRecord> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TGoogleFusionTableInputReader.class);

    private Iterator<List<Object>> rows;

    private IndexedRecord currentRecord;

    /**
     * Converts row retrieved from data source to Avro format {@link IndexedRecord}
     */
    private RowConverter converter;

    /**
     * Represents state of this Reader: whether it was started or not
     */
    private boolean started = false;

    /**
     * Represents state of this Reader: whether it has more records
     */
    private boolean hasMore = false;

    /**
     * Constructor sets {@link Source} of this {@link Reader}
     * 
     * @param source {@link Source} of this {@link Reader}
     */
    protected TGoogleFusionTableInputReader(TGoogleFusionTableInputSource source) {
        super(source);
    }

    @Override
    public boolean start() throws IOException {
        try {
            Fusiontables fusionTables = getCurrentSource().getConnection();
            String query = getCurrentSource().getQuery();
            Sql sql = fusionTables.query().sql(query);
            LOGGER.debug("execute query: " + query);
            Sqlresponse response = sql.execute();
            rows = response.getRows().iterator();
            started = true;
            return advance();
        } catch (GeneralSecurityException e) {
            throw new IOException(e);
        }
    }

    @Override
    public boolean advance() throws IOException {
        if (!started) {
            throw new IllegalStateException("Reader wasn't started");
        }
        hasMore = rows.hasNext();
        if (hasMore) {
            List<Object> row = rows.next();
            currentRecord = getConverter().convertToAvro(row);
        }
        return hasMore;
    }

    @Override
    public IndexedRecord getCurrent() throws NoSuchElementException {
        return null;
    }

    @Override
    public Map<String, Object> getReturnValues() {
        return null;
    }

    @Override
    public TGoogleFusionTableInputSource getCurrentSource() {
        return (TGoogleFusionTableInputSource) super.getCurrentSource();
    }

    /**
     * Returns implementation of {@link AvroConverter}, creates it if it doesn't
     * exist.
     * 
     * @return converter
     */
    private RowConverter getConverter() {
        if (converter == null) {
            converter = new RowConverter(getCurrentSource().getDesignSchema());
        }
        return converter;
    }

}
