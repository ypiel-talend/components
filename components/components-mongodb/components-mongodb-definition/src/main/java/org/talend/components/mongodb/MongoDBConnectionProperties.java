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
package org.talend.components.mongodb;

import static org.talend.components.mongodb.common.MongoDBDefinition.SOURCE_OR_SINK_CLASS;
import static org.talend.components.mongodb.common.MongoDBDefinition.USE_CURRENT_JVM_PROPS;
import static org.talend.components.mongodb.common.MongoDBDefinition.getSandboxedInstance;
import static org.talend.daikon.properties.presentation.Widget.widget;
import static org.talend.daikon.properties.property.PropertyFactory.newBoolean;
import static org.talend.daikon.properties.property.PropertyFactory.newEnum;
import static org.talend.daikon.properties.property.PropertyFactory.newString;

import org.talend.components.api.properties.ComponentPropertiesImpl;
import org.talend.components.api.properties.ComponentReferenceProperties;
import org.talend.components.common.KerberosProperties;
import org.talend.components.common.UserPasswordProperties;
import org.talend.components.mongodb.common.MongoDBRuntimeSourceOrSink;
import org.talend.components.mongodb.common.ReplicaSetTable;
import org.talend.components.mongodb.tmongodbconnection.TMongoDBConnectionDefinition;
import org.talend.daikon.i18n.GlobalI18N;
import org.talend.daikon.i18n.I18nMessages;
import org.talend.daikon.properties.PresentationItem;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.ValidationResultMutable;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;
import org.talend.daikon.properties.service.Repository;
import org.talend.daikon.sandbox.SandboxedInstance;

public class MongoDBConnectionProperties extends ComponentPropertiesImpl {

    protected static final I18nMessages MESSAGES = GlobalI18N.getI18nMessageProvider()
            .getI18nMessages(MongoDBConnectionProperties.class);

    private String repositoryLocation;

    public enum DBVersion {
        MONGODB_2_5_X,
        MONGODB_2_6_X,
        MONGODB_3_0_X,
        MONGODB_3_2_X
    }

    public enum AuthenticationMechanism {
        NEGOTIATE_MEC,
        PLAIN_MEC,
        SCRAMSHA1_MEC,
        KERBEROS_MEC
    }

    public Property<DBVersion> dbVersion = newEnum("dbVersion", DBVersion.class);

    public Property<Boolean> useReplicaSet = newBoolean("useReplicaSet");

    public ReplicaSetTable replicaSetTable = new ReplicaSetTable("replicaSetTable");

    public Property<String> host = newString("host").setRequired();

    public Property<Integer> port = PropertyFactory.newInteger("port", 27017);

    public Property<String> database = newString("database").setRequired();

    public Property<Boolean> useSSL = newBoolean("useSSL");

    public Property<Boolean> requiredAuthentication = newBoolean("requiredAuthentication");

    public Property<AuthenticationMechanism> authenticationMechanism = newEnum("authenticationMechanism",
            AuthenticationMechanism.class);

    public Property<Boolean> setAuthenticationDatabase = newBoolean("setAuthenticationDatabase");

    public Property<String> authenticationDatabase = newString("authenticationDatabase");

    public UserPasswordProperties userPassword = new UserPasswordProperties("userPassword");

    public KerberosProperties kerberos = new KerberosProperties("kerberos");

    public Property<Boolean> queryOptionNoTimeout = newBoolean("queryOptionNoTimeout");

    public ComponentReferenceProperties<MongoDBConnectionProperties> referencedComponent = new ComponentReferenceProperties<>(
            "referencedComponent", TMongoDBConnectionDefinition.COMPONENT_NAME);

    // Only for the wizard use
    public Property<String> name = newString("name").setRequired();

    public PresentationItem testConnection = new PresentationItem("testConnection");

    public static final String FORM_WIZARD = "Wizard";

    public MongoDBConnectionProperties(String name) {
        super(name);
    }

    @Override
    public void setupProperties() {
        super.setupProperties();
        dbVersion.setValue(DBVersion.MONGODB_3_2_X);
        authenticationMechanism.setValue(AuthenticationMechanism.NEGOTIATE_MEC);
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form mainForm = new Form(this, Form.MAIN);
        mainForm.addRow(widget(dbVersion).setWidgetType(Widget.ENUMERATION_WIDGET_TYPE));
        mainForm.addRow(useReplicaSet);
        mainForm.addRow(widget(replicaSetTable).setWidgetType(Widget.TABLE_WIDGET_TYPE));
        mainForm.addRow(host);
        mainForm.addColumn(port);
        mainForm.addRow(database);
        mainForm.addRow(useSSL);

        mainForm.addRow(requiredAuthentication);
        mainForm.addRow(widget(authenticationMechanism).setWidgetType(Widget.ENUMERATION_WIDGET_TYPE));
        mainForm.addRow(setAuthenticationDatabase);
        mainForm.addRow(authenticationDatabase);
        mainForm.addRow(userPassword.getForm(Form.MAIN));
        mainForm.addRow(kerberos.getForm(Form.MAIN));

        Form advancedForm = Form.create(this, Form.ADVANCED);
        advancedForm.addRow(queryOptionNoTimeout);

        Form wizardForm = Form.create(this, FORM_WIZARD);
        wizardForm.addRow(name);
        wizardForm.addRow(widget(dbVersion).setWidgetType(Widget.ENUMERATION_WIDGET_TYPE));
        wizardForm.addRow(useReplicaSet);
        wizardForm.addRow(widget(replicaSetTable).setWidgetType(Widget.TABLE_WIDGET_TYPE));
        wizardForm.addRow(host);
        wizardForm.addColumn(port);
        wizardForm.addRow(database);
        wizardForm.addRow(requiredAuthentication);

        wizardForm.addRow(requiredAuthentication);
        wizardForm.addRow(widget(authenticationMechanism).setWidgetType(Widget.ENUMERATION_WIDGET_TYPE));
        wizardForm.addRow(setAuthenticationDatabase);
        wizardForm.addRow(authenticationDatabase);
        wizardForm.addRow(userPassword.getForm(Form.MAIN));
        wizardForm.addRow(kerberos.getForm(Form.MAIN));
        wizardForm.addRow(widget(testConnection).setLongRunning(true).setWidgetType(Widget.BUTTON_WIDGET_TYPE));

        // A form for a reference to a connection, used in a tMongoDBInput for example
        Form refForm = Form.create(this, Form.REFERENCE);
        Widget compListWidget = widget(referencedComponent).setWidgetType(Widget.COMPONENT_REFERENCE_WIDGET_TYPE);
        refForm.addRow(compListWidget);
        refForm.addRow(mainForm);

    }

    @Override
    public void refreshLayout(Form form) {
        super.refreshLayout(form);

        String refComponentIdValue = getReferencedComponentId();
        boolean useOtherConnection = refComponentIdValue != null
                && refComponentIdValue.startsWith(TMongoDBConnectionDefinition.COMPONENT_NAME);
        if (useOtherConnection) {
            if (form.getName().equals(Form.MAIN) || form.getName().equals(Form.ADVANCED)) {
                form.setHidden(true);
            }
        } else {
            if (form.getName().equals(Form.MAIN) || form.getName().equals(Form.ADVANCED)) {
                form.setHidden(false);
            }
            if (form.getName().equals(Form.MAIN) || form.getName().equals(MongoDBConnectionProperties.FORM_WIZARD)) {

                DBVersion version = dbVersion.getValue();
                if (DBVersion.MONGODB_2_5_X.equals(version)) {
                    authenticationMechanism.setPossibleValues(AuthenticationMechanism.KERBEROS_MEC,
                            AuthenticationMechanism.KERBEROS_MEC);
                } else if (DBVersion.MONGODB_2_6_X.equals(version)) {
                    authenticationMechanism.setPossibleValues(AuthenticationMechanism.KERBEROS_MEC,
                            AuthenticationMechanism.KERBEROS_MEC, AuthenticationMechanism.PLAIN_MEC);
                } else {
                    authenticationMechanism.setPossibleValues(AuthenticationMechanism.values());
                }

                form.getWidget(replicaSetTable).setVisible(useReplicaSet.getValue());
                boolean useAuthentication = requiredAuthentication.getValue();
                boolean useAuthenticationDatabase = setAuthenticationDatabase.getValue();
                form.getWidget(authenticationMechanism).setVisible(useAuthentication);

                boolean authDatabaseVisible = useAuthentication
                        && (AuthenticationMechanism.NEGOTIATE_MEC.equals(authenticationMechanism.getValue())
                                || AuthenticationMechanism.SCRAMSHA1_MEC.equals(authenticationMechanism.getValue()));
                form.getWidget(setAuthenticationDatabase).setVisible(authDatabaseVisible);
                form.getWidget(authenticationDatabase).setVisible(authDatabaseVisible && useAuthenticationDatabase);

                form.getWidget(userPassword).setVisible(
                        useAuthentication && (AuthenticationMechanism.NEGOTIATE_MEC.equals(authenticationMechanism.getValue())
                                || AuthenticationMechanism.SCRAMSHA1_MEC.equals(authenticationMechanism.getValue())
                                || AuthenticationMechanism.PLAIN_MEC.equals(authenticationMechanism.getValue())));
                form.getWidget(kerberos).setVisible(
                        useAuthentication && AuthenticationMechanism.KERBEROS_MEC.equals(authenticationMechanism.getValue()));
            }
        }
    }

    public void afterDbVersion() {
        refreshLayout(getForm(Form.MAIN));
        refreshLayout(getForm(FORM_WIZARD));
        refreshLayout(getForm(Form.REFERENCE));
    }

    public void afterAuthenticationMechanism() {
        refreshLayout(getForm(Form.MAIN));
        refreshLayout(getForm(Form.REFERENCE));
        refreshLayout(getForm(FORM_WIZARD));
    }

    public void afterUseReplicaSet() {
        refreshLayout(getForm(Form.MAIN));
        refreshLayout(getForm(FORM_WIZARD));
        refreshLayout(getForm(Form.REFERENCE));
    }

    public void afterRequiredAuthentication() {
        refreshLayout(getForm(Form.MAIN));
        refreshLayout(getForm(Form.REFERENCE));
        refreshLayout(getForm(FORM_WIZARD));
    }

    public void afterSetAuthenticationDatabase() {
        refreshLayout(getForm(Form.MAIN));
        refreshLayout(getForm(FORM_WIZARD));
        refreshLayout(getForm(Form.REFERENCE));
    }

    public void afterReferencedComponent() {
        refreshLayout(getForm(Form.MAIN));
        refreshLayout(getForm(Form.REFERENCE));
        refreshLayout(getForm(Form.ADVANCED));
    }

    public ValidationResult validateTestConnection() throws Exception {
        try (SandboxedInstance sandboxedInstance = getSandboxedInstance(SOURCE_OR_SINK_CLASS, USE_CURRENT_JVM_PROPS)) {
            MongoDBRuntimeSourceOrSink ss = (MongoDBRuntimeSourceOrSink) sandboxedInstance.getInstance();
            ss.initialize(null, MongoDBConnectionProperties.this);
            ValidationResultMutable vr = new ValidationResultMutable(ss.validate(null));
            if (vr.getStatus() == ValidationResult.Result.OK) {
                vr.setMessage(MESSAGES.getMessage("connection.success"));
                getForm(FORM_WIZARD).setAllowForward(true);
            } else {
                getForm(FORM_WIZARD).setAllowForward(false);
            }
            return vr;
        }
    }

    public String getReferencedComponentId() {
        return referencedComponent.componentInstanceId.getValue();
    }

    public MongoDBConnectionProperties getReferencedConnectionProperties() {
        return referencedComponent.getReference();
    }

    public ValidationResult afterFormFinishWizard(Repository<Properties> repo) throws Exception {
        String connRepLocation = repo.storeProperties(this, this.getName(), repositoryLocation, null);
        repo.storeProperties(this, this.name.getValue(), connRepLocation, null);
        return ValidationResult.OK;
    }

    public MongoDBConnectionProperties setRepositoryLocation(String location) {
        repositoryLocation = location;
        return this;
    }
}
