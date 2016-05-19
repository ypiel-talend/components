// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.salesforce.tsalesforceinput;

import static org.talend.daikon.properties.PropertyFactory.*;

import java.util.Collections;
import java.util.Set;

import org.talend.components.api.component.PropertyPathConnector;
import org.talend.components.salesforce.SalesforceConnectionModuleProperties;
import org.talend.daikon.properties.Property;
import org.talend.daikon.properties.presentation.Form;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TSalesforceInputProperties extends SalesforceConnectionModuleProperties {

    public enum QueryMode {
        QUERY,
        BULK;
    }

    public Property<QueryMode> queryMode = newEnum("queryMode", QueryMode.class);

    public Property<String> condition = newProperty("condition"); //$NON-NLS-1$

    public Property<Boolean> manualQuery = newBoolean("manualQuery"); //$NON-NLS-1$

    public Property<String> query = newProperty("query"); //$NON-NLS-1$

    public Property<Boolean> includeDeleted = newBoolean("includeDeleted"); //$NON-NLS-1$

    //
    // Advanced
    //
    public Property<Integer> batchSize = newInteger("batchSize"); //$NON-NLS-1$

    public Property<String> normalizeDelimiter = newProperty("normalizeDelimiter"); //$NON-NLS-1$

    public Property<String> columnNameDelimiter = newProperty("columnNameDelimiter"); //$NON-NLS-1$

    public TSalesforceInputProperties(@JsonProperty("name") String name) {
        super(name);
    }

    @Override
    public void setupProperties() {
        super.setupProperties();
        batchSize.setValue(250);
        queryMode.setValue(QueryMode.QUERY);
        normalizeDelimiter.setValue(";");
        columnNameDelimiter.setValue("_");

    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form mainForm = getForm(Form.MAIN);
        mainForm.addRow(queryMode);
        mainForm.addRow(condition);
        mainForm.addRow(manualQuery);
        mainForm.addRow(query);
        mainForm.addRow(includeDeleted);

        Form advancedForm = getForm(Form.ADVANCED);
        advancedForm.addRow(batchSize);
        advancedForm.addRow(normalizeDelimiter);
        advancedForm.addRow(columnNameDelimiter);
    }

    public void afterQueryMode() {
        refreshLayout(getForm(Form.MAIN));
        refreshLayout(getForm(Form.ADVANCED));
    }

    public void afterManualQuery() {
        refreshLayout(getForm(Form.MAIN));
    }

    @Override
    public void refreshLayout(Form form) {
        super.refreshLayout(form);
        if (form.getName().equals(Form.MAIN)) {
            form.getWidget(includeDeleted.getName())
                    .setHidden(!(queryMode.getValue() != null && queryMode.getValue().equals(QueryMode.QUERY)));

            form.getWidget(query.getName()).setHidden(!manualQuery.getValue());
            form.getWidget(condition.getName()).setHidden(manualQuery.getValue());
        }
        if (Form.ADVANCED.equals(form.getName())) {
            boolean isBulkQuery = queryMode.getValue().equals(QueryMode.BULK);
            form.getWidget(normalizeDelimiter.getName()).setHidden(isBulkQuery);
            form.getWidget(columnNameDelimiter.getName()).setHidden(isBulkQuery);
            form.getWidget(batchSize.getName()).setHidden(isBulkQuery);
            form.getChildForm(connection.getName()).getWidget(connection.bulkConnection.getName()).setHidden(true);
            form.getChildForm(connection.getName()).getWidget(connection.timeout.getName()).setHidden(isBulkQuery);
        }
    }

    @Override
    protected Set<PropertyPathConnector> getAllSchemaPropertiesConnectors(boolean isOutputConnection) {
        if (isOutputConnection) {
            return Collections.singleton(MAIN_CONNECTOR);
        } else {
            return Collections.EMPTY_SET;
        }
    }

}
