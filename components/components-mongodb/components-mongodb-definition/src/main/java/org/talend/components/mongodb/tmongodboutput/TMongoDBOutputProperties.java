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

package org.talend.components.mongodb.tmongodboutput;

import static org.talend.daikon.properties.presentation.Widget.widget;
import static org.talend.daikon.properties.property.PropertyFactory.newBoolean;
import static org.talend.daikon.properties.property.PropertyFactory.newEnum;
import static org.talend.daikon.properties.property.PropertyFactory.newProperty;

import java.util.Collections;
import java.util.Set;

import org.talend.components.api.component.ISchemaListener;
import org.talend.components.api.component.PropertyPathConnector;
import org.talend.components.mongodb.MongoDBConnectionProperties;
import org.talend.components.mongodb.common.MongoDBBaseProperties;
import org.talend.components.mongodb.common.NodePathMappingTable;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;

public class TMongoDBOutputProperties extends MongoDBBaseProperties {

    public Property<Boolean> dropExistCollection = newBoolean("dropExistCollection");

    public enum WriteConcern {
        ACKNOWLEDGED,
        UNACKNOWLEDGED,
        JOURNALED,
        REPLICA_ACKNOWLEDGED
    }

    public Property<Boolean> setWriteConcern = newBoolean("setWriteConcern");

    public Property<WriteConcern> writeConcern = newEnum("writeConcern", WriteConcern.class);

    public enum BulkWriteType {
        Unordered,
        Ordered
    }

    public Property<Boolean> setBulkWrite = newBoolean("setBulkWrite");

    public Property<BulkWriteType> bulkWriteType = newEnum("bulkWriteType", BulkWriteType.class);

    public Property<String> bulkWriteSize = newProperty("bulkWriteSize"); //$NON-NLS-1$

    public enum DataAction {
        INSERT,
        UPDATE,
        SET,
        UPSERT,
        UPSERT_WITH_SET,
        DELETE
    }

    public Property<DataAction> dataAction = newEnum("dataAction", DataAction.class);

    public NodePathMappingTable mapping = new NodePathMappingTable("mapping", true);

    public Property<Boolean> dieOnError = newBoolean("dieOnError");

    // TODO add advanced setting

    public TMongoDBOutputProperties(String name) {
        super(name);
    }

    @Override
    public void setupProperties() {
        super.setupProperties();
        writeConcern.setValue(WriteConcern.ACKNOWLEDGED);
        bulkWriteType.setValue(BulkWriteType.Unordered);
        bulkWriteSize.setValue("1000");
        dataAction.setValue(DataAction.INSERT);
        collection.setSchemaListener(new ISchemaListener() {

            @Override
            public void afterSchema() {
                beforeMapping();
            }
        });
    }

    public void beforeMapping() {
        mapping.updateTable(getFieldNames(collection.main.schema.getValue()));
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form mainForm = getForm(Form.MAIN);
        mainForm.addRow(dropExistCollection);
        mainForm.addRow(setWriteConcern);
        mainForm.addRow(widget(writeConcern).setWidgetType(Widget.ENUMERATION_WIDGET_TYPE));
        mainForm.addRow(setBulkWrite);
        mainForm.addRow(widget(bulkWriteType).setWidgetType(Widget.ENUMERATION_WIDGET_TYPE));
        mainForm.addRow(bulkWriteSize);
        mainForm.addRow(widget(dataAction).setWidgetType(Widget.ENUMERATION_WIDGET_TYPE));
        mainForm.addRow(widget(mapping).setWidgetType(Widget.TABLE_WIDGET_TYPE));
        mainForm.addRow(dieOnError);

        Form advancedForm = getForm(Form.ADVANCED);
        // advancedForm.addRow(extSortAgg);
    }

    @Override
    public void refreshLayout(Form form) {
        super.refreshLayout(form);
        if (form.getName().equals(Form.MAIN)) {
            form.getWidget(writeConcern).setVisible(setWriteConcern.getValue());
            boolean setBulkWriteVisible = !MongoDBConnectionProperties.DBVersion.MONGODB_2_5_X
                    .equals(connection.dbVersion.getValue());
            form.getWidget(setBulkWrite).setVisible(setBulkWriteVisible);
            form.getWidget(bulkWriteType).setVisible(setBulkWriteVisible && setBulkWrite.getValue());
            form.getWidget(bulkWriteSize).setVisible(setBulkWriteVisible && setBulkWrite.getValue());

        } else if (form.getName().equals(Form.ADVANCED)) {
            // TODO
        }
    }

    @Override
    protected Set<PropertyPathConnector> getAllSchemaPropertiesConnectors(boolean isOutputConnection) {
        return Collections.singleton(MAIN_CONNECTOR);
    }

    public void afterSetWriteConcern() {
        refreshLayout(getForm(Form.MAIN));
    }

    public void afterSetBulkWrite() {
        refreshLayout(getForm(Form.MAIN));
    }

}
