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

package org.talend.components.mongodb.tmongodbinput;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;
import org.talend.components.api.component.Connector;
import org.talend.components.api.component.PropertyPathConnector;
import org.talend.components.api.service.ComponentService;
import org.talend.components.api.test.ComponentTestUtils;
import org.talend.components.mongodb.MongoDBConnectionProperties;
import org.talend.components.mongodb.MongoDBTestBase;
import org.talend.components.mongodb.common.MongoDBTestUtils;
import org.talend.daikon.properties.presentation.Form;

public class TMongoDBInputPropertiesTest extends MongoDBTestBase {

    @Test
    public void beforeMapping() throws Throwable {
        TMongoDBInputProperties properties = new TMongoDBInputProperties("properties");
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

        // Change schema to add new column
        properties.collection.main.schema.setValue(MongoDBTestUtils.BASIC_SCHEMA_01);
        componentService.afterProperty(properties.collection.main.schema.getName(), properties.collection.main);
        assertNotNull(properties.mapping.nodePath.getValue());
        assertEquals(4, properties.mapping.columnName.getValue().size());
        assertTrue(properties.mapping.columnName.getValue().containsAll(Arrays.asList("Id", "Name", "Age")));
        assertEquals(4, properties.mapping.nodePath.getValue().size());
        assertTrue(properties.mapping.nodePath.getValue().containsAll(Arrays.asList(null, null, null)));
    }

    @Test
    public void testSetupLayout() throws Exception {
        TMongoDBInputProperties properties = new TMongoDBInputProperties("properties");
        properties.init();

        ComponentTestUtils.checkSerialize(properties, errorCollector);
        Form mainForm = properties.getForm(Form.MAIN);
        assertTrue(mainForm.getWidget(properties.collection).isVisible());
        assertTrue(mainForm.getWidget(properties.setReadPreference).isVisible());
        assertTrue(mainForm.getWidget(properties.readPreference).isHidden());
        assertTrue(mainForm.getWidget(properties.queryType).isVisible());
        assertTrue(mainForm.getWidget(properties.aggStages).isHidden());
        assertTrue(mainForm.getWidget(properties.mapping).isVisible());
        assertTrue(mainForm.getWidget(properties.sort).isVisible());
        assertTrue(mainForm.getWidget(properties.limit).isVisible());

        Form advancedForm = properties.getForm(Form.ADVANCED);
        assertTrue(advancedForm.getWidget(properties.extSortAgg).isHidden());

        // Default value
        assertNull(properties.collection.collectionName.getValue());
        assertFalse(properties.setReadPreference.getValue());
        assertEquals(TMongoDBInputProperties.ReadPreference.PRIMARY, properties.readPreference.getValue());
        assertEquals(TMongoDBInputProperties.QueryType.FIND_QUERY, properties.queryType.getValue());
        assertEquals("{}", properties.query.getValue());
        assertNull(properties.aggStages.stage.getValue());
        assertNull(properties.mapping.columnName.getValue());
        assertNull(properties.mapping.nodePath.getValue());
        assertNull(properties.sort.columnName.getValue());
        assertNull(properties.sort.order.getValue());

    }

    @Test
    public void testRefreshLayout() throws Throwable {
        TMongoDBInputProperties properties = new TMongoDBInputProperties("properties");
        properties.init();

        Form mainForm = properties.getForm(Form.MAIN);
        Form advanceForm = properties.getForm(Form.ADVANCED);
        ComponentService componentService = getComponentService();
        // SetReadPreference
        properties.setReadPreference.setValue(true);
        componentService.afterProperty(properties.setReadPreference.getName(), properties);
        assertTrue(mainForm.getWidget(properties.readPreference).isVisible());
        // QueryType
        properties.queryType.setValue(TMongoDBInputProperties.QueryType.AGGREGATION_QUERY);
        componentService.afterProperty(properties.queryType.getName(), properties);
        assertTrue(mainForm.getWidget(properties.aggStages).isVisible());
        assertTrue(mainForm.getWidget(properties.sort).isHidden());
        assertTrue(mainForm.getWidget(properties.limit).isHidden());
        assertTrue(advanceForm.getWidget(properties.extSortAgg).isVisible());
        // DBVersion
        assertEquals(TMongoDBInputProperties.QueryType.AGGREGATION_QUERY, properties.queryType.getValue());
        assertEquals(MongoDBConnectionProperties.DBVersion.MONGODB_3_2_X, properties.connection.dbVersion.getValue());
        properties.connection.dbVersion.setValue(MongoDBConnectionProperties.DBVersion.MONGODB_2_5_X);
        componentService.afterProperty(properties.connection.dbVersion.getName(), properties.connection);
        componentService.beforePropertyActivate(properties.queryType.getName(), properties);
        assertTrue(properties.queryType.getPossibleValues().contains(TMongoDBInputProperties.QueryType.FIND_QUERY));
        assertEquals(1, properties.queryType.getPossibleValues().size());
        assertEquals(TMongoDBInputProperties.QueryType.FIND_QUERY, properties.queryType.getValue());

        // QueryType is changed
        componentService.afterProperty(properties.queryType.getName(), properties);
        assertTrue(mainForm.getWidget(properties.aggStages).isHidden());
        assertTrue(mainForm.getWidget(properties.sort).isVisible());
        assertTrue(mainForm.getWidget(properties.limit).isVisible());
        assertTrue(advanceForm.getWidget(properties.extSortAgg).isHidden());
    }

    @Test
    public void getAllSchemaPropertiesConnectors() throws Exception {
        TMongoDBInputProperties properties = new TMongoDBInputProperties("properties");
        properties.init();
        assertEquals(1, properties.getAllSchemaPropertiesConnectors(true).size());
        PropertyPathConnector mainConnector = (PropertyPathConnector) properties.getAllSchemaPropertiesConnectors(true)
                .toArray()[0];
        assertEquals(Connector.MAIN_NAME, mainConnector.getName());
        assertEquals("collection.main", mainConnector.getPropertyPath());
        assertEquals(0, properties.getAllSchemaPropertiesConnectors(false).size());
    }

    @Test
    public void testI18nForEnumProperty() {
        TMongoDBInputProperties properties = new TMongoDBInputProperties("root");
        properties.init();
        properties.setupProperties();

        assertEquals("Primary",
                properties.readPreference.getPossibleValuesDisplayName(TMongoDBInputProperties.ReadPreference.PRIMARY));
        assertEquals("Primary preferred",
                properties.readPreference.getPossibleValuesDisplayName(TMongoDBInputProperties.ReadPreference.PRIMARY_PREFERRED));
        assertEquals("Secondary",
                properties.readPreference.getPossibleValuesDisplayName(TMongoDBInputProperties.ReadPreference.SECONDARY));
        assertEquals("Secondary preferred", properties.readPreference
                .getPossibleValuesDisplayName(TMongoDBInputProperties.ReadPreference.SECONDARY_PREFERRED));
        assertEquals("Nearest",
                properties.readPreference.getPossibleValuesDisplayName(TMongoDBInputProperties.ReadPreference.NEAREST));

        assertEquals("Find Query",
                properties.queryType.getPossibleValuesDisplayName(TMongoDBInputProperties.QueryType.FIND_QUERY));
        assertEquals("Aggregation Pipeline Query",
                properties.queryType.getPossibleValuesDisplayName(TMongoDBInputProperties.QueryType.AGGREGATION_QUERY));

    }

    @Ignore("Not implement")
    @Test
    public void testBeforeCollectionName() {
        // TODO maybe move to other part
    }

    @Ignore("Not implement")
    @Test
    public void testAfterCollectionName() {
        // TODO maybe move to other part
    }

}