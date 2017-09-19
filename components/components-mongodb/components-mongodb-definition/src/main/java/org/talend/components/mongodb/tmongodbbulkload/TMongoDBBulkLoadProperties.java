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
package org.talend.components.mongodb.tmongodbbulkload;

import static org.talend.daikon.properties.presentation.Widget.widget;
import static org.talend.daikon.properties.property.PropertyFactory.newBoolean;
import static org.talend.daikon.properties.property.PropertyFactory.newEnum;
import static org.talend.daikon.properties.property.PropertyFactory.newProperty;
import static org.talend.daikon.properties.property.PropertyFactory.newString;

import org.talend.components.api.component.ISchemaListener;
import org.talend.components.api.properties.ComponentPropertiesImpl;
import org.talend.components.mongodb.MongoDBCollectionProperties;
import org.talend.components.mongodb.MongoDBConnectionProperties;
import org.talend.components.mongodb.MongoDBProvideConnectionProperties;
import org.talend.components.mongodb.common.AdditionalArgsTable;
import org.talend.components.mongodb.common.MongoDBBaseProperties;
import org.talend.components.mongodb.common.UpsertFieldTable;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;

public class TMongoDBBulkLoadProperties extends ComponentPropertiesImpl implements MongoDBProvideConnectionProperties {

    public MongoDBConnectionProperties connection = new MongoDBConnectionProperties("connection");

    public MongoDBCollectionProperties collection;

    public Property<String> mongoDBHome = newProperty("mongoDBHome");

    public Property<Boolean> useLocalDBPath = newBoolean("useLocalDBPath");

    public Property<String> localDBPath = newProperty("localDBPath");

    public Property<Boolean> specifyReplicateSet = newBoolean("specifyReplicateSet");

    public Property<String> replicateName = newProperty("replicateName");

    public Property<String> krbAuthDatabase = newProperty("krbAuthDatabase");

    public Property<Boolean> dropExistCollection = newBoolean("dropExistCollection");

    public Property<String> dataFile = newString("dataFile");

    public enum FileType {
        csv,
        json,
        tsv
    }

    public Property<FileType> fileType = newEnum("fileType", FileType.class);

    public enum DataAction {
        INSERT,
        UPSERT
    }

    public Property<DataAction> dataAction = newEnum("dataAction", DataAction.class);

    public UpsertFieldTable upsertField = new UpsertFieldTable("upsertField");

    public Property<Boolean> headerLine = newBoolean("headerLine");

    public Property<Boolean> ignoreBlanks = newBoolean("ignoreBlanks");

    public Property<Boolean> jsonArray = newBoolean("jsonArray");

    public Property<Boolean> printLog = newBoolean("printLog");

    public AdditionalArgsTable additionalArgs = new AdditionalArgsTable("additionalArgs");

    public TMongoDBBulkLoadProperties(String name) {
        super(name);
        collection = new MongoDBCollectionProperties("collection");
        collection.connection = getConnectionProperties();
    }

    @Override
    public void setupProperties() {
        super.setupProperties();
        connection.dbVersion.setValue(MongoDBConnectionProperties.DBVersion.MONGODB_3_2_X);
        connection.authenticationMechanism.setPossibleValues( //
                MongoDBConnectionProperties.AuthenticationMechanism.MONGODBCR_MEC, //
                MongoDBConnectionProperties.AuthenticationMechanism.PLAIN_MEC, //
                MongoDBConnectionProperties.AuthenticationMechanism.SCRAMSHA1_MEC, //
                MongoDBConnectionProperties.AuthenticationMechanism.KERBEROS_MEC);
        connection.kerberos.userPrincipal.setValue("mongouser@EXAMPLE.COM");
        connection.kerberos.realm.setValue("mongodb");
        connection.kerberos.kdcServer.setValue("talend-mongo");
        krbAuthDatabase.setValue("$external");
        dataAction.setValue(DataAction.INSERT);
        fileType.setValue(FileType.csv);
        collection.setSchemaListener(new ISchemaListener() {

            @Override
            public void afterSchema() {
                beforeUpsertField();
            }
        });
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form mainForm = new Form(this, Form.MAIN);
        mainForm.addRow(widget(mongoDBHome).setWidgetType(Widget.DIRECTORY_WIDGET_TYPE));
        mainForm.addRow(useLocalDBPath);
        mainForm.addRow(widget(localDBPath).setWidgetType(Widget.DIRECTORY_WIDGET_TYPE));
        mainForm.addRow(connection.getForm(Form.MAIN));
        mainForm.addRow(krbAuthDatabase);
        mainForm.addRow(specifyReplicateSet);
        mainForm.addRow(replicateName);
        mainForm.addRow(collection.getForm(Form.REFERENCE));
        mainForm.addRow(dropExistCollection);
        mainForm.addRow(widget(dataAction).setWidgetType(Widget.ENUMERATION_WIDGET_TYPE));
        mainForm.addRow(widget(upsertField).setWidgetType(Widget.TABLE_WIDGET_TYPE));
        mainForm.addRow(widget(dataFile).setWidgetType(Widget.FILE_WIDGET_TYPE));
        mainForm.addRow(widget(fileType).setWidgetType(Widget.ENUMERATION_WIDGET_TYPE));
        mainForm.addRow(headerLine);
        mainForm.addRow(ignoreBlanks);
        mainForm.addRow(jsonArray);
        mainForm.addRow(printLog);

        Form advancedForm = Form.create(this, Form.ADVANCED);
        advancedForm.addRow(widget(additionalArgs).setWidgetType(Widget.TABLE_WIDGET_TYPE));

    }

    @Override
    public void refreshLayout(Form form) {
        super.refreshLayout(form);

        if (form.getName().equals(Form.MAIN)) {
            connection.dbVersion.setValue(MongoDBConnectionProperties.DBVersion.MONGODB_3_2_X);
            form.getWidget(localDBPath).setVisible(useLocalDBPath.getValue());
            boolean useLocalDB = useLocalDBPath.getValue();
            boolean useReplica = connection.useReplicaSet.getValue();
            Form connForm = form.getChildForm(connection.getName());
            connForm.getWidget(connection.host.getName()).setHidden(useLocalDB || useReplica);
            connForm.getWidget(connection.port.getName()).setHidden(useLocalDB || useReplica);
            connForm.getWidget(connection.dbVersion.getName()).setHidden();
            connForm.getWidget(connection.useReplicaSet.getName()).setHidden(useLocalDB);
            connForm.getWidget(connection.useSSL.getName())
                    .setHidden(useLocalDB || (connection.requiredAuthentication.getValue()
                            && MongoDBConnectionProperties.AuthenticationMechanism.KERBEROS_MEC
                                    .equals(connection.authenticationMechanism.getValue())));
            connForm.getWidget(connection.replicaSetTable.getName())
                    .setVisible(connection.useReplicaSet.getValue() && !useLocalDB);

            form.getWidget(specifyReplicateSet).setVisible(!useLocalDB && useReplica);
            form.getWidget(replicateName).setVisible(!useLocalDB && useReplica && specifyReplicateSet.getValue());

            boolean needKerbosAuth = connection.requiredAuthentication.getValue()
                    && MongoDBConnectionProperties.AuthenticationMechanism.KERBEROS_MEC
                            .equals(connection.authenticationMechanism.getValue());

            form.getWidget(krbAuthDatabase).setVisible(needKerbosAuth);

            form.getWidget(upsertField).setVisible(DataAction.UPSERT.equals(dataAction.getValue()));
            boolean isCsvOrTsv = FileType.csv.equals(fileType.getValue()) || FileType.tsv.equals(fileType.getValue());
            form.getWidget(headerLine).setVisible(isCsvOrTsv);
            form.getWidget(ignoreBlanks).setVisible(isCsvOrTsv);
            form.getWidget(jsonArray).setHidden(isCsvOrTsv);

        }
    }

    public void afterUseLocalDBPath() {
        refreshLayout(getForm(Form.MAIN));
    }

    public void afterSpecifyReplicateSet() {
        refreshLayout(getForm(Form.MAIN));
    }

    public void afterFileType() {
        refreshLayout(getForm(Form.MAIN));
    }

    public void afterDataAction() {
        refreshLayout(getForm(Form.MAIN));
    }

    public void beforeUpsertField() {
        upsertField.columnName.setPossibleValues(MongoDBBaseProperties.getFieldNames(collection.main.schema.getValue()));
    }

    @Override
    public MongoDBConnectionProperties getConnectionProperties() {
        return this.connection;
    }
}
