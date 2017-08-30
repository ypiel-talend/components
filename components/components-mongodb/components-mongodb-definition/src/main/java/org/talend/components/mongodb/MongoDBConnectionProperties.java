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

import static org.talend.daikon.properties.presentation.Widget.widget;
import static org.talend.daikon.properties.property.PropertyFactory.newEnum;
import static org.talend.daikon.properties.property.PropertyFactory.newString;

import java.util.List;

import org.talend.components.api.properties.ComponentPropertiesImpl;
import org.talend.components.api.properties.ComponentReferenceProperties;
import org.talend.components.common.UserPasswordProperties;
import org.talend.components.mongodb.tmongodbconnection.TMongoDBConnectionDefinition;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;

public class MongoDBConnectionProperties extends ComponentPropertiesImpl {

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

    public Property<Boolean> useReplicaSet = PropertyFactory.newBoolean("useReplicaSet");

    public ReplicaSetTable replicaSetTable = new ReplicaSetTable("replicaSetTable");

    public Property<String> host = newString("host").setRequired();

    public Property<Integer> port = PropertyFactory.newInteger("port", 27017);

    public Property<String> database = newString("database").setRequired();

    public Property<Boolean> useSSL = PropertyFactory.newBoolean("useSSL");

    public Property<Boolean> requiredAuthentication = PropertyFactory.newBoolean("requiredAuthentication");

    public Property<List<String>> authenticationMechanism = PropertyFactory.newStringList("authenticationMechanism");

    public Property<Boolean> setAuthenticationDatabase = PropertyFactory.newBoolean("setAuthenticationDatabase");

    public Property<String> authenticationDatabase = newString("authenticationDatabase");

    public UserPasswordProperties userPassword = new UserPasswordProperties("userPassword");

    public ComponentReferenceProperties<MongoDBConnectionProperties> referencedComponent = new ComponentReferenceProperties<>(
            "referencedComponent", TMongoDBConnectionDefinition.COMPONENT_NAME);

    public static final String FORM_WIZARD = "Wizard";

    public MongoDBConnectionProperties(String name) {
        super(name);
    }

    @Override
    public void setupProperties() {
        super.setupProperties();
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
        mainForm.addRow(authenticationMechanism);
        mainForm.addRow(setAuthenticationDatabase);
        mainForm.addRow(authenticationDatabase);
        mainForm.addRow(userPassword.getForm(Form.MAIN));

        Form refForm = Form.create(this, Form.REFERENCE);
        Widget compListWidget = widget(referencedComponent).setWidgetType(Widget.COMPONENT_REFERENCE_WIDGET_TYPE);
        refForm.addRow(mainForm);

        // Form wizardForm = Form.create(this, FORM_WIZARD);
        // wizardForm.addRow(name);
        // wizardForm.addRow(accountName);

    }

    @Override
    public void refreshLayout(Form form) {
        super.refreshLayout(form);

        String refComponentIdValue = getReferencedComponentId();
        if (form.getName().equals(Form.MAIN) || form.getName().equals(MongoDBConnectionProperties.FORM_WIZARD)) {
            form.getWidget(replicaSetTable).setHidden(useReplicaSet.getValue());
            boolean useAuthentication = !requiredAuthentication.getValue();
            boolean useAuthenticationDatabase = !setAuthenticationDatabase.getValue();
            form.getWidget(authenticationMechanism).setHidden(useAuthentication);
            form.getWidget(setAuthenticationDatabase).setHidden(useAuthentication);
            form.getWidget(authenticationDatabase).setHidden(useAuthentication || useAuthenticationDatabase);
            form.getWidget(userPassword).setHidden(useAuthentication);
        }
    }

    public void afterUseReplicaSetAddress() {
        refreshLayout(getForm(Form.MAIN));
        refreshLayout(getForm(Form.REFERENCE));
    }

    public void afterRequiredAuthentication() {
        refreshLayout(getForm(Form.MAIN));
        refreshLayout(getForm(Form.REFERENCE));
        // refreshLayout(getForm(FORM_WIZARD));
    }

    public void afterSetAuthenticationDatabase() {
        refreshLayout(getForm(Form.MAIN));
        refreshLayout(getForm(Form.REFERENCE));
    }

    public String getReferencedComponentId() {
        return referencedComponent.componentInstanceId.getValue();
    }

    public MongoDBConnectionProperties getReferencedConnectionProperties() {
        MongoDBConnectionProperties refProps = referencedComponent.getReference();
        if (refProps != null) {
            return refProps;
        }
        return null;
    }
}
