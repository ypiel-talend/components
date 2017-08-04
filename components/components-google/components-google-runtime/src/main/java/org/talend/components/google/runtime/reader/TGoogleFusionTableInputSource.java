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

public class TGoogleFusionTableInputSource implements BoundedSource {

    private TGoogleFusionTableInputProperties properties;

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
        return null;
    }

    @Override
    public BoundedReader createReader(RuntimeContainer adaptor) {
        return null;
    }

    @Override
    public List<NamedThing> getSchemaNames(RuntimeContainer container) throws IOException {
        return null;
    }

    @Override
    public Schema getEndpointSchema(RuntimeContainer container, String schemaName) throws IOException {
        return null;
    }

    @Override
    public List<? extends BoundedSource> splitIntoBundles(long desiredBundleSizeBytes, RuntimeContainer adaptor)
            throws Exception {
        return null;
    }

    @Override
    public long getEstimatedSizeBytes(RuntimeContainer adaptor) {
        return 0;
    }

    @Override
    public boolean producesSortedKeys(RuntimeContainer adaptor) {
        return false;
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
