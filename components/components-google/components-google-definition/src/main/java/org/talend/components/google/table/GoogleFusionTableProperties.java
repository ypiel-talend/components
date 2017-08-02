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

import org.talend.components.common.SchemaProperties;
import org.talend.daikon.properties.PropertiesImpl;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;

/**
 * Stores Google Fusion Table id and its schema, which is specified by user.
 * Also has read-only property to show generated SQL Query
 */
public class GoogleFusionTableProperties extends PropertiesImpl {

    private static final long serialVersionUID = -4353914708920008905L;

    public final Property<String> tableId = PropertyFactory.newString("tableId");

    public final SchemaProperties tableSchema = new SchemaProperties("tableSchema");
    
    public final Property<String> query = PropertyFactory.newString("query");
    
    /**
     * Constructor sets properties name
     * 
     * @param name name of this properties instance
     */
    public GoogleFusionTableProperties(String name) {
        super(name);
    }

}
