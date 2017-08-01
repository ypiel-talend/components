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

import org.talend.daikon.properties.PropertiesImpl;

/**
 * Stores properties required for connection. Google Fusion Tables support OAuth 2.0 authorization, so 
 * client id and client secret are required. These values will be used to get authorization code and 
 * access and refresh tokens after.
 * <p>
 * Also it stores path to local credentials store. This store is secure store on local file system used
 * to store access and refresh token
 */
public class GoogleFusionTableConnectionProperties extends PropertiesImpl {

    /**
     * Constructor sets properties name
     * 
     * @param name name of this properties instance
     */
    public GoogleFusionTableConnectionProperties(String name) {
        super(name);
    }

}
