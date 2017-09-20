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

package org.talend.components.mongodb.wizard;

import static org.talend.components.mongodb.common.MongoDBDefinition.SOURCE_OR_SINK_CLASS;
import static org.talend.components.mongodb.common.MongoDBDefinition.USE_CURRENT_JVM_PROPS;
import static org.talend.components.mongodb.common.MongoDBDefinition.getSandboxedInstance;
import static org.talend.daikon.properties.presentation.Widget.widget;
import static org.talend.daikon.properties.property.PropertyFactory.newProperty;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.avro.Schema;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.TypeLiteral;
import org.talend.components.api.exception.ComponentException;
import org.talend.components.api.properties.ComponentPropertiesImpl;
import org.talend.components.mongodb.MongoDBCollectionProperties;
import org.talend.components.mongodb.MongoDBConnectionProperties;
import org.talend.components.mongodb.MongoDBRuntimeSourceOrSink;
import org.talend.components.mongodb.error.MongoDBErrorCodes;
import org.talend.daikon.NamedThing;
import org.talend.daikon.SimpleNamedThing;
import org.talend.daikon.i18n.GlobalI18N;
import org.talend.daikon.i18n.I18nMessages;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.ValidationResultMutable;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.service.Repository;
import org.talend.daikon.sandbox.SandboxedInstance;

public class MongoDBCollectionListProperties extends ComponentPropertiesImpl {

    protected static final I18nMessages MESSAGES = GlobalI18N.getI18nMessageProvider()
            .getI18nMessages(MongoDBCollectionListProperties.class);

    public static final String FORM_DATABASE = "Database";

    public static final String FORM_COLLECTION = "Collection";

    private MongoDBConnectionProperties connection = new MongoDBConnectionProperties("connection");

    private String repositoryLocation;

    public List<NamedThing> databaseNames;

    public List<NamedThing> collectionNames;

    //
    // Properties
    //
    public Property<List<NamedThing>> selectedDatabaseNames = newProperty(new TypeLiteral<List<NamedThing>>() {
    }, "selectedDatabaseNames"); //$NON-NLS-1$

    public Property<List<NamedThing>> selectedCollectionNames = newProperty(new TypeLiteral<List<NamedThing>>() {
    }, "selectedCollectionNames"); //$NON-NLS-1$

    public MongoDBCollectionListProperties(String name) {
        super(name);
    }

    public MongoDBCollectionListProperties setConnection(MongoDBConnectionProperties connection) {
        connection.database.setRequired(false);
        this.connection = connection;
        return this;
    }

    public MongoDBCollectionListProperties setRepositoryLocation(String location) {
        repositoryLocation = location;
        return this;
    }

    @Override
    public void setupLayout() {
        super.setupLayout();

        Form databaseForm = Form.create(this, FORM_DATABASE);
        // Since this is a repeating property it has a list of values
        databaseForm.addRow(widget(selectedDatabaseNames).setWidgetType(Widget.NAME_SELECTION_AREA_WIDGET_TYPE));
        refreshLayout(databaseForm);

        Form collectionForm = Form.create(this, FORM_COLLECTION);
        // Since this is a repeating property it has a list of values
        collectionForm.addRow(widget(selectedCollectionNames).setWidgetType(Widget.NAME_SELECTION_AREA_WIDGET_TYPE));
        refreshLayout(collectionForm);
    }

    /**
     * Show database list
     */
    public ValidationResult beforeFormPresentDatabase() throws Exception {
        try (SandboxedInstance sandboxedInstance = getSandboxedInstance(SOURCE_OR_SINK_CLASS, USE_CURRENT_JVM_PROPS)) {
            if (StringUtils.isEmpty(connection.database.getValue())) {
                MongoDBRuntimeSourceOrSink ss = (MongoDBRuntimeSourceOrSink) sandboxedInstance.getInstance();
                ss.initialize(null, connection);
                try {
                    databaseNames = ss.getDatabaseNames(null);
                } catch (IOException e) {
                    ValidationResultMutable vr = new ValidationResultMutable();
                    vr.setMessage(e.getMessage());
                    vr.setStatus(ValidationResult.Result.ERROR);
                    getForm(FORM_DATABASE).setAllowBack(true);
                    return vr;
                }
            } else {
                NamedThing dbName = new SimpleNamedThing(connection.database.getValue(), connection.database.getValue());
                databaseNames = Arrays.asList(dbName);
            }
            selectedDatabaseNames.setPossibleValues(databaseNames);
            getForm(FORM_DATABASE).setAllowBack(true);
            getForm(FORM_DATABASE).setAllowForward(true);
            getForm(FORM_DATABASE).setAllowFinish(true);
            return ValidationResult.OK;
        }
    }

    /**
     * Show collections list for specify database
     */
    public ValidationResult beforeFormPresentCollection() throws Exception {
        try (SandboxedInstance sandboxedInstance = getSandboxedInstance(SOURCE_OR_SINK_CLASS, USE_CURRENT_JVM_PROPS)) {
            MongoDBRuntimeSourceOrSink ss = (MongoDBRuntimeSourceOrSink) sandboxedInstance.getInstance();
            ss.initialize(null, connection);
            try {
                collectionNames = ss.getCollectionNames(null, selectedDatabaseNames.getValue());
            } catch (IOException e) {
                ValidationResultMutable vr = new ValidationResultMutable();
                vr.setMessage(e.getMessage());
                vr.setStatus(ValidationResult.Result.ERROR);
                getForm(FORM_COLLECTION).setAllowBack(true);
                return vr;
            }
            selectedCollectionNames.setPossibleValues(collectionNames);
            getForm(FORM_COLLECTION).setAllowBack(true);
            getForm(FORM_COLLECTION).setAllowFinish(true);
            return ValidationResult.OK;
        }
    }

    public ValidationResult afterFormFinishCollection(Repository<Properties> repo) throws Exception {
        try (SandboxedInstance sandboxedInstance = getSandboxedInstance(SOURCE_OR_SINK_CLASS, USE_CURRENT_JVM_PROPS)) {

            MongoDBRuntimeSourceOrSink ss = (MongoDBRuntimeSourceOrSink) sandboxedInstance.getInstance();
            ss.initialize(null, connection);
            ValidationResult vr = ss.validate(null);
            if (vr.getStatus() != ValidationResult.Result.OK) {
                return vr;
            }

            connection.databaseNames = selectedDatabaseNames.getValue();
            connection.collectionNames = selectedCollectionNames.getValue();

            String connRepLocation = repo.storeProperties(connection, connection.name.getValue(), repositoryLocation, null);

            Map<String, List<String>> dbCollections = ss.getDBCollectionMapping(selectedCollectionNames);

            Set<String> dbNames = dbCollections.keySet();
            for (String dbName : dbNames) {
                connection.database.setValue(dbName);
                String dbRepLocation = repo.storeProperties(connection, dbName, connRepLocation, null);
                for (String collectionName : dbCollections.get(dbName)) {
                    MongoDBCollectionProperties modProps = new MongoDBCollectionProperties(collectionName);
                    modProps.connection = connection;
                    modProps.init();
                    Schema schema = ss.getEndpointSchema(null, collectionName);
                    modProps.collectionName.setValue(collectionName);
                    modProps.main.schema.setValue(schema);
                    repo.storeProperties(modProps, collectionName, dbRepLocation, "main.schema");
                }
            }

            return ValidationResult.OK;
        }
    }
}
