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
import java.util.Arrays;
import java.util.List;

import org.apache.avro.Schema;
import org.talend.components.api.component.runtime.BoundedReader;
import org.talend.components.api.component.runtime.BoundedSource;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.google.tgooglefusiontableinput.TGoogleFusionTableInputProperties;
import org.talend.daikon.NamedThing;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.ValidationResult.Result;

import com.google.api.services.fusiontables.Fusiontables;

public class TGoogleFusionTableInputSource implements BoundedSource {

    private static final long serialVersionUID = 1L;

    private TGoogleFusionTableInputProperties properties;

    private transient Fusiontables fusionTables;

    @Override
    public ValidationResult initialize(RuntimeContainer container, ComponentProperties properties) {
        assert properties instanceof TGoogleFusionTableInputProperties;
        this.properties = (TGoogleFusionTableInputProperties) properties;
        if (isCredentialsSet()) {
            return ValidationResult.OK;
        } else {
            return new ValidationResult(Result.ERROR, "Client Id or/and Client Secret wasn't set");
        }
    }

    @Override
    public ValidationResult validate(RuntimeContainer container) {
        try {
            getConnection();
        } catch (IOException | GeneralSecurityException e) {
            return new ValidationResult(Result.ERROR, "Error during connection establishment");
        }
        return ValidationResult.OK;
    }

    @Override
    public BoundedReader createReader(RuntimeContainer adaptor) {
        return null;
    }

    @Override
    public List<NamedThing> getSchemaNames(RuntimeContainer container) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Schema getEndpointSchema(RuntimeContainer container, String schemaName) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<? extends BoundedSource> splitIntoBundles(long desiredBundleSizeBytes, RuntimeContainer adaptor)
            throws Exception {
        // This source won't be splitted
        return Arrays.asList(this);
    }

    @Override
    public long getEstimatedSizeBytes(RuntimeContainer adaptor) {
        // This will be ignored since the source will never be split.
        return 0;
    }

    @Override
    public boolean producesSortedKeys(RuntimeContainer adaptor) {
        return false;
    }

    /**
     * Returns Fusion Tables connection. Creates it, if it doesn't exist yet
     * 
     * @return Fusion Tables connection
     * @throws GeneralSecurityException
     * @throws IOException
     */
    Fusiontables getConnection() throws GeneralSecurityException, IOException {
        if (fusionTables == null) {
            fusionTables = new FusionTablesCreator(properties.getClientId(), properties.getClientSecret()).createFusionTables();
        }
        return fusionTables;
    }
    
    String getQuery() {
        return properties.tableProperties.query.getValue();
    }

    /**
     * Checks whether credentials were set. If they are empty, method returns false
     * 
     * @return true when credentials are set, false - otherwise
     */
    private boolean isCredentialsSet() {
        String clientId = properties.connectionProperties.clientId.getValue();
        if (clientId == null || clientId.trim().isEmpty()) {
            return false;
        }
        String clientSecret = properties.connectionProperties.clientSecret.getValue();
        if (clientSecret == null || clientSecret.trim().isEmpty()) {
            return false;
        }
        return true;
    }

}
