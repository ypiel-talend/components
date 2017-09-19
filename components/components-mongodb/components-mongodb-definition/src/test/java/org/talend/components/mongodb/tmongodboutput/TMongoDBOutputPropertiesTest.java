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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;
import org.talend.components.api.component.Connector;
import org.talend.components.api.component.PropertyPathConnector;
import org.talend.components.api.service.ComponentService;
import org.talend.components.api.test.ComponentTestUtils;
import org.talend.components.mongodb.MongoDBConnectionProperties;
import org.talend.components.mongodb.MongoDBTestBase;
import org.talend.components.mongodb.common.MongoDBTestUtils;
import org.talend.daikon.properties.presentation.Form;

public class TMongoDBOutputPropertiesTest extends MongoDBTestBase {

    @Test
    public void testBeforeMapping() throws Throwable {
        TMongoDBOutputProperties properties = new TMongoDBOutputProperties("properties");
        properties.init();

        ComponentTestUtils.checkSerialize(properties, errorCollector);
        Form mainForm = properties.getForm(Form.MAIN);
        ComponentService componentService = getComponentService();

        properties.collection.main.schema.setValue(MongoDBTestUtils.BASIC_SCHEMA);
        componentService.afterProperty(properties.collection.main.schema.getName(), properties.collection.main);
        assertNotNull(properties.mapping.nodePath.getValue());
        assertEquals(3, properties.mapping.columnName.getValue().size());
        assertTrue(properties.mapping.columnName.getValue().containsAll(Arrays.asList("Id", "Name", "Age")));
        assertEquals(3, properties.mapping.nodePath.getValue().size());
        assertTrue(properties.mapping.nodePath.getValue().containsAll(Arrays.asList(null, null, null)));
    }

    @Test
    public void setupLayout() throws Exception {
        TMongoDBOutputProperties properties = new TMongoDBOutputProperties("properties");
        properties.init();

        ComponentTestUtils.checkSerialize(properties, errorCollector);
        Form mainForm = properties.getForm(Form.MAIN);
        assertTrue(mainForm.getWidget(properties.collection).isVisible());
        assertTrue(mainForm.getWidget(properties.dropExistCollection).isVisible());
        assertTrue(mainForm.getWidget(properties.setWriteConcern).isVisible());
        assertTrue(mainForm.getWidget(properties.writeConcern).isHidden());
        assertTrue(mainForm.getWidget(properties.setBulkWrite).isVisible());
        assertTrue(mainForm.getWidget(properties.bulkWriteType).isHidden());
        assertTrue(mainForm.getWidget(properties.bulkWriteSize).isHidden());
        assertTrue(mainForm.getWidget(properties.dataAction).isVisible());
        assertTrue(mainForm.getWidget(properties.updateAll).isHidden());
        assertTrue(mainForm.getWidget(properties.mapping).isVisible());
        assertTrue(mainForm.getWidget(properties.dieOnError).isVisible());

        // Default value
        assertNull(properties.collection.collectionName.getValue());
        assertFalse(properties.dropExistCollection.getValue());
        assertFalse(properties.setWriteConcern.getValue());
        assertFalse(properties.setBulkWrite.getValue());
        assertFalse(properties.dropExistCollection.getValue());
        assertEquals(TMongoDBOutputProperties.BulkWriteType.Unordered, properties.bulkWriteType.getValue());
        assertTrue(properties.bulkWriteType.getPossibleValues()
                .containsAll(Arrays.asList(TMongoDBOutputProperties.BulkWriteType.values())));
        assertEquals("1000", properties.bulkWriteSize.getValue());
        assertEquals(TMongoDBOutputProperties.DataAction.INSERT, properties.dataAction.getValue());
        assertTrue(properties.dataAction.getPossibleValues()
                .containsAll(Arrays.asList(TMongoDBOutputProperties.DataAction.values())));
        assertNull(properties.mapping.columnName.getValue());
        assertNull(properties.mapping.nodePath.getValue());
        assertFalse(properties.dieOnError.getValue());

    }

    @Test
    public void testRefreshLayout() throws Throwable {
        TMongoDBOutputProperties properties = new TMongoDBOutputProperties("properties");
        properties.init();

        Form mainForm = properties.getForm(Form.MAIN);
        Form advanceForm = properties.getForm(Form.ADVANCED);
        ComponentService componentService = getComponentService();

        // SetWriteConcern
        properties.setWriteConcern.setValue(true);
        componentService.afterProperty(properties.setWriteConcern.getName(), properties);
        assertTrue(mainForm.getWidget(properties.writeConcern).isVisible());
        // SetWriteConcern
        properties.setBulkWrite.setValue(true);
        componentService.afterProperty(properties.setBulkWrite.getName(), properties);
        assertTrue(mainForm.getWidget(properties.bulkWriteType).isVisible());
        assertTrue(mainForm.getWidget(properties.bulkWriteSize).isVisible());

        // DBVersion changed
        assertEquals(MongoDBConnectionProperties.DBVersion.MONGODB_3_2_X, properties.connection.dbVersion.getValue());
        properties.connection.dbVersion.setValue(MongoDBConnectionProperties.DBVersion.MONGODB_2_5_X);
        properties.refreshLayout(mainForm);
        assertTrue(mainForm.getWidget(properties.setBulkWrite).isHidden());
        assertTrue(mainForm.getWidget(properties.bulkWriteType).isHidden());
        assertTrue(mainForm.getWidget(properties.bulkWriteSize).isHidden());

        // DataAction changed
        properties.dataAction.setValue(TMongoDBOutputProperties.DataAction.SET);
        componentService.afterProperty(properties.dataAction.getName(), properties);
        assertTrue(mainForm.getWidget(properties.updateAll).isVisible());

    }

    @Test
    public void testI18nForEnumProperty() {
        TMongoDBOutputProperties properties = new TMongoDBOutputProperties("root");
        properties.init();
        properties.setupProperties();

        assertEquals("ACKNOWLEDGED",
                properties.writeConcern.getPossibleValuesDisplayName(TMongoDBOutputProperties.WriteConcern.ACKNOWLEDGED));
        assertEquals("UNACKNOWLEDGED",
                properties.writeConcern.getPossibleValuesDisplayName(TMongoDBOutputProperties.WriteConcern.UNACKNOWLEDGED));

        assertEquals("JOURNALED",
                properties.writeConcern.getPossibleValuesDisplayName(TMongoDBOutputProperties.WriteConcern.JOURNALED));

        assertEquals("REPLICA_ACKNOWLEDGED",
                properties.writeConcern.getPossibleValuesDisplayName(TMongoDBOutputProperties.WriteConcern.REPLICA_ACKNOWLEDGED));

        assertEquals("Insert", properties.dataAction.getPossibleValuesDisplayName(TMongoDBOutputProperties.DataAction.INSERT));
        assertEquals("Update", properties.dataAction.getPossibleValuesDisplayName(TMongoDBOutputProperties.DataAction.UPDATE));
        assertEquals("Set", properties.dataAction.getPossibleValuesDisplayName(TMongoDBOutputProperties.DataAction.SET));
        assertEquals("Upsert", properties.dataAction.getPossibleValuesDisplayName(TMongoDBOutputProperties.DataAction.UPSERT));
        assertEquals("Upsert with set",
                properties.dataAction.getPossibleValuesDisplayName(TMongoDBOutputProperties.DataAction.UPSERT_WITH_SET));
        assertEquals("Delete", properties.dataAction.getPossibleValuesDisplayName(TMongoDBOutputProperties.DataAction.DELETE));

    }

    @Test
    public void testGetAllSchemaPropertiesConnectors() throws Exception {
        TMongoDBOutputProperties properties = new TMongoDBOutputProperties("properties");
        properties.init();
        assertEquals(1, properties.getAllSchemaPropertiesConnectors(false).size());
        assertEquals(1, properties.getAllSchemaPropertiesConnectors(true).size());
        PropertyPathConnector mainConnector = (PropertyPathConnector) properties.getAllSchemaPropertiesConnectors(true)
                .toArray()[0];
        assertEquals(Connector.MAIN_NAME, mainConnector.getName());
        assertEquals("collection.main", mainConnector.getPropertyPath());
    }

}