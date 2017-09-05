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

import static org.talend.components.mongodb.common.MongoDBDefinition.SOURCE_OR_SINK_CLASS;
import static org.talend.components.mongodb.common.MongoDBDefinition.USE_CURRENT_JVM_PROPS;
import static org.talend.components.mongodb.common.MongoDBDefinition.getSandboxedInstance;
import static org.talend.daikon.properties.presentation.Widget.widget;
import static org.talend.daikon.properties.property.PropertyFactory.newString;

import java.util.List;

import org.apache.avro.Schema;
import org.talend.components.api.component.ISchemaListener;
import org.talend.components.api.properties.ComponentPropertiesImpl;
import org.talend.components.common.SchemaProperties;
import org.talend.components.mongodb.MongoDBConnectionProperties;
import org.talend.daikon.NamedThing;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.ValidationResultMutable;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.StringProperty;
import org.talend.daikon.sandbox.SandboxedInstance;

public class MongoDBCollectionProperties extends ComponentPropertiesImpl {

    public MongoDBConnectionProperties connection = new MongoDBConnectionProperties("connection");

    //
    // Properties
    //
    public StringProperty collectionName = newString("collectionName"); //$NON-NLS-1$

    public ISchemaListener schemaListener;

    public SchemaProperties main = new SchemaProperties("main") {

        public void afterSchema() {
            if (schemaListener != null) {
                schemaListener.afterSchema();
            }
        }
    };

    public MongoDBCollectionProperties(String name) {
        super(name);
    }

    @Override
    public void setupProperties() {
        super.setupProperties();
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form collectionForm = Form.create(this, Form.MAIN);
        collectionForm.addRow(widget(collectionName).setWidgetType(Widget.NAME_SELECTION_AREA_WIDGET_TYPE));
        refreshLayout(collectionForm);

        Form collectionRefForm = Form.create(this, Form.REFERENCE);
        collectionRefForm
                .addRow(widget(collectionName).setWidgetType(Widget.NAME_SELECTION_REFERENCE_WIDGET_TYPE).setLongRunning(true));

        collectionRefForm.addRow(main.getForm(Form.REFERENCE));
        refreshLayout(collectionRefForm);
    }

    public void setSchemaListener(ISchemaListener schemaListener) {
        this.schemaListener = schemaListener;
    }

    // consider beforeActivate and beforeRender (change after to afterActivate)

    public ValidationResult beforeModuleName() throws Exception {
        try (SandboxedInstance sandboxedInstance = getSandboxedInstance(SOURCE_OR_SINK_CLASS, USE_CURRENT_JVM_PROPS)) {
            MongoDBRuntimeSourceOrSink ss = (MongoDBRuntimeSourceOrSink) sandboxedInstance.getInstance();
            // ss.initialize(null, connection);
            ValidationResultMutable vr = new ValidationResultMutable();
            try {
                List<NamedThing> collectionNames = ss.getSchemaNames(null);
                collectionName.setPossibleNamedThingValues(collectionNames);
            } catch (Exception ex) {
                vr.setMessage(ex.getMessage());
                vr.setStatus(ValidationResult.Result.ERROR);
            }
            return ValidationResult.OK;
        }
    }

    public ValidationResult afterModuleName() {
        try (SandboxedInstance sandboxedInstance = getSandboxedInstance(SOURCE_OR_SINK_CLASS, USE_CURRENT_JVM_PROPS)) {

            MongoDBRuntimeSourceOrSink ss = (MongoDBRuntimeSourceOrSink) sandboxedInstance.getInstance();
            // ss.initialize(null, connection);
            ValidationResultMutable vr = new ValidationResultMutable();
            try {
                Schema schema = ss.getEndpointSchema(null, collectionName.getStringValue());
                main.schema.setValue(schema);
            } catch (Exception ex) {
                vr.setMessage(ex.getMessage());
                vr.setStatus(ValidationResult.Result.ERROR);
            }
            return ValidationResult.OK;
        }
    }
}
