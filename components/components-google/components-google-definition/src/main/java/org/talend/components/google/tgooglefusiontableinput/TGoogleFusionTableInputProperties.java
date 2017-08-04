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
package org.talend.components.google.tgooglefusiontableinput;

import java.util.Set;

import org.talend.components.api.component.PropertyPathConnector;
import org.talend.components.common.FixedConnectorsComponentProperties;
import org.talend.components.google.connection.GoogleFusionTableConnectionProperties;
import org.talend.components.google.table.GoogleFusionTableProperties;
import org.talend.daikon.properties.presentation.Form;

public class TGoogleFusionTableInputProperties extends FixedConnectorsComponentProperties {

    private static final long serialVersionUID = -1908527018782175776L;

    public final GoogleFusionTableConnectionProperties connectionProperties = new GoogleFusionTableConnectionProperties(
            "connectionProperties");
    
    public final GoogleFusionTableProperties tableProperties = new GoogleFusionTableProperties("tableProperties");

    /**
     * Constructor sets properties name
     * 
     * @param name name of this properties instance
     */
    public TGoogleFusionTableInputProperties(String name) {
        super(name);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setupLayout() {
        super.setupLayout();
        
        Form mainForm = new Form(this, Form.MAIN);
        mainForm.addRow(connectionProperties.getForm(Form.MAIN));
        mainForm.addRow(tableProperties.getForm(Form.MAIN));
    }

    @Override
    protected Set<PropertyPathConnector> getAllSchemaPropertiesConnectors(boolean isOutputConnection) {
        return null;
    }

}
