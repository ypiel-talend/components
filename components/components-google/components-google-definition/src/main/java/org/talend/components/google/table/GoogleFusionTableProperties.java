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
package org.talend.components.google.table;

import org.talend.daikon.properties.PropertiesImpl;

/**
 * Stores Google Fusion Table id and its schema, which is specified by user.
 * Also has read-only property to show generated SQL Query
 */
public class GoogleFusionTableProperties extends PropertiesImpl {

    /**
     * Constructor sets properties name
     * 
     * @param name name of this properties instance
     */
    public GoogleFusionTableProperties(String name) {
        super(name);
    }

}
