//  ============================================================================
//
//  Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
//  This source code is available under agreement available at
//  %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
//  You should have received a copy of the agreement
//  along with this program; if not, write to Talend SA
//  9 rue Pages 92150 Suresnes, France
//
//  ============================================================================

package org.talend.components.mongodb.wizard;

import static org.talend.components.mongodb.common.MongoDBDefinition.SOURCE_OR_SINK_CLASS;
import static org.talend.components.mongodb.common.MongoDBDefinition.USE_CURRENT_JVM_PROPS;
import static org.talend.components.mongodb.common.MongoDBDefinition.getSandboxedInstance;
import static org.talend.daikon.properties.presentation.Widget.widget;
import static org.talend.daikon.properties.property.PropertyFactory.newProperty;

import java.util.List;

import org.apache.avro.Schema;
import org.apache.commons.lang3.reflect.TypeLiteral;
import org.talend.components.api.properties.ComponentPropertiesImpl;
import org.talend.components.mongodb.MongoDBConnectionProperties;
import org.talend.components.mongodb.common.MongoDBCollectionProperties;
import org.talend.components.mongodb.common.MongoDBRuntimeSourceOrSink;
import org.talend.daikon.NamedThing;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.service.Repository;
import org.talend.daikon.sandbox.SandboxedInstance;

public class MongoDBCollectionListProperties extends ComponentPropertiesImpl {

    public MongoDBConnectionProperties connection = new MongoDBConnectionProperties("connection");

    private String repositoryLocation;

    private List<NamedThing> collectionNames;

    //
    // Properties
    //
    public Property<List<NamedThing>> selectedCollectionNames = newProperty(new TypeLiteral<List<NamedThing>>() {
    }, "selectedCollectionNames"); //$NON-NLS-1$

    public MongoDBCollectionListProperties(String name) {
        super(name);
    }

    public MongoDBCollectionListProperties setRepositoryLocation(String location) {
        repositoryLocation = location;
        return this;
    }

    public String getRepositoryLocation() {
        return repositoryLocation;
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form collectionForm = Form.create(this, Form.MAIN);
        // Since this is a repeating property it has a list of values
        collectionForm.addRow(widget(selectedCollectionNames).setWidgetType(Widget.NAME_SELECTION_AREA_WIDGET_TYPE));
        refreshLayout(collectionForm);
    }

    public void beforeFormPresentMain() throws Exception {
        try (SandboxedInstance sandboxedInstance = getSandboxedInstance(SOURCE_OR_SINK_CLASS, USE_CURRENT_JVM_PROPS)) {
            MongoDBRuntimeSourceOrSink ss = (MongoDBRuntimeSourceOrSink) sandboxedInstance.getInstance();
            collectionNames = ss.getSchemaNames(null);
            selectedCollectionNames.setPossibleValues(collectionNames);
            getForm(Form.MAIN).setAllowBack(true);
            getForm(Form.MAIN).setAllowFinish(true);
        }
    }

    public ValidationResult afterFormFinishMain(Repository<Properties> repo) throws Exception {
        try (SandboxedInstance sandboxedInstance = getSandboxedInstance(SOURCE_OR_SINK_CLASS, USE_CURRENT_JVM_PROPS)) {

            MongoDBRuntimeSourceOrSink ss = (MongoDBRuntimeSourceOrSink) sandboxedInstance.getInstance();
            ss.initialize(null, this);
            ValidationResult vr = ss.validate(null);
            if (vr.getStatus() != ValidationResult.Result.OK) {
                return vr;
            }

            String connRepLocation = repo.storeProperties(connection, connection.name.getValue(), repositoryLocation, null);

            for (NamedThing nl : selectedCollectionNames.getValue()) {
                String collectionName = nl.getName();
                MongoDBCollectionProperties modProps = new MongoDBCollectionProperties(collectionName);
                modProps.connection = connection;
                modProps.init();
                Schema schema = ss.getEndpointSchema(null, collectionName);
                modProps.collectionName.setValue(collectionName);
                modProps.main.schema.setValue(schema);
                repo.storeProperties(modProps, nl.getName(), connRepLocation, "main.schema");
            }
            return ValidationResult.OK;
        }
    }
}
