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

import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.talend.components.common.SchemaProperties;
import org.talend.daikon.avro.AvroUtils;
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

    public final SchemaProperties tableSchema = new SchemaProperties("tableSchema") {
        
        /**
         * Computes and sets SQL query after Table Schema is changed
         */
        public void afterSchema() {
            query.setValue(buildQuery());
            refreshLayout(getForm(Form.MAIN));
        }
        
    };

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
    
    public void afterTableId() {
        query.setValue(buildQuery());
        refreshLayout(getForm(Form.MAIN));
    }
    
    /**
     * Builds SQL query to retrieve rows from fusion table
     * If Table Id or Table Schema wasn't set, it returns empty string
     * Builds SQL query according following patern: "SELECT [fields] FROM [tableId]"
     * 
     * @return SQL query
     */
    protected String buildQuery() {
        if (!isTableIdSet() || !isTableSchemaSet()) {
            return "";
        }
        
        StringBuilder selectQuery = new StringBuilder();
        selectQuery.append("SELECT ");
        selectQuery.append(schemaFields());
        selectQuery.append(" FROM ");
        selectQuery.append(tableId.getValue());
        return selectQuery.toString();
    }
    
    /**
     * Checks whether Table Id property is set
     * 
     * @return true if it is set, false otherwise
     */
    private boolean isTableIdSet() {
        String tableIdValue = tableId.getValue();
        return tableIdValue != null && !tableIdValue.trim().isEmpty();
    }
    
    /**
     * Checks whether Table Schema property is set and not empty
     * 
     * @return true if it is set and not empty, false otherwise
     */
    private boolean isTableSchemaSet() {
        Schema tableSchemaValue = tableSchema.schema.getValue();
        return tableSchemaValue != null && !AvroUtils.isSchemaEmpty(tableSchemaValue);
    }

    /**
     * @return a string of table schema field names separated by comma
     */
    private String schemaFields() {
        List<Field> fields = tableSchema.schema.getValue().getFields();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.size() - 1; i++) {
            sb.append(fields.get(i).name());
            sb.append(',');
        }
        sb.append(fields.get(fields.size()-1).name());
        return sb.toString();
    }
    
}
