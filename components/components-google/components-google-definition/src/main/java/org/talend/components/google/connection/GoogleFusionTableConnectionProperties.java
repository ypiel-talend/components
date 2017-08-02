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
package org.talend.components.google.connection;

import java.util.EnumSet;

import org.talend.daikon.properties.PropertiesImpl;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;

/**
 * Stores properties required for connection. Google Fusion Tables support OAuth 2.0 authorization, so
 * client id and client secret are required. These values will be used to get authorization code and
 * access and refresh tokens after.
 */
public class GoogleFusionTableConnectionProperties extends PropertiesImpl {

    private static final long serialVersionUID = 5928871310221499828L;

    /**
     * Constructor sets properties name
     * 
     * @param name name of this properties instance
     */
    public GoogleFusionTableConnectionProperties(String name) {
        super(name);
    }

    public final Property<String> clientId = PropertyFactory.newString("clientId");

    public final Property<String> clientSecret = PropertyFactory.newString("clientSecret")
            .setFlags(EnumSet.of(Property.Flags.ENCRYPT, Property.Flags.SUPPRESS_LOGGING));

    /**
     * {@inheritDoc}
     */
    @Override
    public void setupProperties() {
        super.setupProperties();
        clientId.setValue("");
        clientSecret.setValue("");
    }

}
