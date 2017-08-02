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
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void setupProperties() {
        super.setupProperties();
        tableId.setValue("");
        query.setValue("");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setupLayout() {
        super.setupLayout();
        Form mainForm = new Form(this, Form.MAIN);
        mainForm.addRow(tableId);
        mainForm.addRow(tableSchema.getForm(Form.REFERENCE));
        mainForm.addRow(Widget.widget(query).setReadonly(true));
    }
}
