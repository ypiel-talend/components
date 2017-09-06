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

package org.talend.components.mongodb.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.avro.Schema;
import org.talend.components.api.component.Connector;
import org.talend.components.api.component.PropertyPathConnector;
import org.talend.components.common.FixedConnectorsComponentProperties;
import org.talend.components.mongodb.MongoDBConnectionProperties;
import org.talend.daikon.properties.presentation.Form;

public abstract class MongoDBBaseProperties extends FixedConnectorsComponentProperties
        implements MongoDBProvideConnectionProperties {

    public MongoDBConnectionProperties connection = new MongoDBConnectionProperties("connection");

    public MongoDBCollectionProperties collection;

    protected transient PropertyPathConnector MAIN_CONNECTOR = new PropertyPathConnector(Connector.MAIN_NAME, "collection.main");

    public MongoDBBaseProperties(String name) {
        super(name);
        // Allow for subclassing
        collection = new MongoDBCollectionProperties("collection");
        collection.connection = getConnectionProperties();
    }

    @Override
    public MongoDBConnectionProperties getConnectionProperties() {
        return this.connection;
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form mainForm = new Form(this, Form.MAIN);
        mainForm.addRow(connection.getForm(Form.REFERENCE));
        mainForm.addRow(collection.getForm(Form.REFERENCE));

        Form advancedForm = new Form(this, Form.ADVANCED);
        advancedForm.addRow(connection.getForm(Form.ADVANCED));
    }

    @Override
    public void refreshLayout(Form form) {
        super.refreshLayout(form);
        for (Form childForm : connection.getForms()) {
            connection.refreshLayout(childForm);
        }
    }

    protected List<String> getFieldNames(Schema schema) {
        List<String> fieldNames = new ArrayList<>();
        if (schema != null) {
            for (Schema.Field f : schema.getFields()) {
                fieldNames.add(f.name());
            }
        }
        return fieldNames;
    }

}
