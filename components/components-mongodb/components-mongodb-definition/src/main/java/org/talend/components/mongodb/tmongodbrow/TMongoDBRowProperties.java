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

package org.talend.components.mongodb.tmongodbrow;

import static org.talend.components.mongodb.common.MongoDBConstants.DYNAMIC_PROPERTY_VALUE;
import static org.talend.daikon.properties.presentation.Widget.widget;
import static org.talend.daikon.properties.property.PropertyFactory.newBoolean;
import static org.talend.daikon.properties.property.PropertyFactory.newProperty;

import java.util.Collections;
import java.util.Set;

import org.talend.components.api.component.Connector;
import org.talend.components.api.component.PropertyPathConnector;
import org.talend.components.common.FixedConnectorsComponentProperties;
import org.talend.components.common.SchemaProperties;
import org.talend.components.mongodb.MongoDBConnectionProperties;
import org.talend.components.mongodb.MongoDBProvideConnectionProperties;
import org.talend.components.mongodb.common.FunctionParametersTable;
import org.talend.components.mongodb.common.KeyValueTable;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;

public class TMongoDBRowProperties extends FixedConnectorsComponentProperties implements MongoDBProvideConnectionProperties {

    public MongoDBConnectionProperties connection = new MongoDBConnectionProperties("connection");

    public Property<Boolean> executeCommand = newBoolean("executeCommand");

    public Property<Boolean> executeKVCommand = newBoolean("executeKVCommand");

    public Property<String> command = newProperty("command");

    public Property<Boolean> executeJSONCommand = newBoolean("executeJSONCommand");

    public KeyValueTable keyValueTable = new KeyValueTable("keyValueTable");

    public Property<String> jsonCommand = newProperty("jsonCommand");

    public Property<String> function = newProperty("function");

    public FunctionParametersTable functionParams = new FunctionParametersTable("functionParams");

    public Property<Boolean> dieOnError = newBoolean("dieOnError");

    public SchemaProperties schema = new SchemaProperties("schema");

    protected transient PropertyPathConnector MAIN_CONNECTOR = new PropertyPathConnector(Connector.MAIN_NAME, "schema");

    public TMongoDBRowProperties(String name) {
        super(name);
    }

    @Override
    public void setupProperties() {
        super.setupProperties();
        jsonCommand.setValue(
                "{createIndexes: 'restaurants', indexes : [{key : {restaurant_id: 1}, name: 'id_index_2', unique: true}]}");
        jsonCommand.setTaggedValue(DYNAMIC_PROPERTY_VALUE, true);
        function.setTaggedValue(DYNAMIC_PROPERTY_VALUE, true);
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form mainForm = new Form(this, Form.MAIN);
        mainForm.addRow(connection.getForm(Form.REFERENCE));
        mainForm.addRow(schema.getForm(Form.REFERENCE));
        mainForm.addRow(executeCommand);
        mainForm.addRow(executeKVCommand);
        mainForm.addRow(executeJSONCommand);
        mainForm.addRow(command);
        mainForm.addRow(widget(keyValueTable).setWidgetType(Widget.TABLE_WIDGET_TYPE));
        mainForm.addRow(widget(jsonCommand).setWidgetType(Widget.TEXT_AREA_WIDGET_TYPE));
        mainForm.addRow(widget(function).setWidgetType(Widget.TEXT_AREA_WIDGET_TYPE));
        mainForm.addRow(widget(functionParams).setWidgetType(Widget.TABLE_WIDGET_TYPE));
        mainForm.addRow(dieOnError);

        Form advancedForm = new Form(this, Form.ADVANCED);
        advancedForm.addRow(connection.getForm(Form.ADVANCED));
    }

    @Override
    public void refreshLayout(Form form) {
        super.refreshLayout(form);
        if (form.getName().equals(Form.MAIN)) {
            form.getWidget(executeKVCommand).setVisible(executeCommand.getValue() && !executeJSONCommand.getValue());
            form.getWidget(executeJSONCommand).setVisible(executeCommand.getValue() && !executeKVCommand.getValue());
            form.getWidget(command)
                    .setVisible(executeCommand.getValue() && !executeKVCommand.getValue() && !executeJSONCommand.getValue());
            form.getWidget(keyValueTable).setVisible(executeCommand.getValue() && executeKVCommand.getValue());
            form.getWidget(jsonCommand).setVisible(executeCommand.getValue() && executeJSONCommand.getValue());
            form.getWidget(function).setHidden(executeCommand.getValue());
            form.getWidget(functionParams).setHidden(executeCommand.getValue());

        }
    }

    @Override
    protected Set<PropertyPathConnector> getAllSchemaPropertiesConnectors(boolean isOutputConnection) {
        if (isOutputConnection) {
            return Collections.EMPTY_SET;
        } else {
            return Collections.singleton(MAIN_CONNECTOR);
        }
    }

    public void afterExecuteCommand() {
        refreshLayout(getForm(Form.MAIN));
    }

    public void afterExecuteKVCommand() {
        refreshLayout(getForm(Form.MAIN));
    }

    public void afterExecuteJSONCommand() {
        refreshLayout(getForm(Form.MAIN));
    }

    @Override
    public MongoDBConnectionProperties getConnectionProperties() {
        return this.connection;
    }

}
