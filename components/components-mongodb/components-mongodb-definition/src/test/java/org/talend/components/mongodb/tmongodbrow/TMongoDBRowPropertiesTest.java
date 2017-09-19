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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.talend.components.api.component.Connector;
import org.talend.components.api.component.PropertyPathConnector;
import org.talend.components.api.service.ComponentService;
import org.talend.components.api.test.ComponentTestUtils;
import org.talend.components.mongodb.MongoDBTestBase;
import org.talend.daikon.properties.presentation.Form;

public class TMongoDBRowPropertiesTest extends MongoDBTestBase {

    @Test
    public void testSetupLayout() throws Throwable {
        TMongoDBRowProperties properties = new TMongoDBRowProperties("properties");
        properties.init();

        Form mainForm = properties.getForm(Form.MAIN);
        Form advanceForm = properties.getForm(Form.ADVANCED);
        ComponentService componentService = getComponentService();

        // ExecuteCommand
        properties.executeCommand.setValue(true);
        componentService.afterProperty(properties.executeCommand.getName(), properties);
        assertTrue(mainForm.getWidget(properties.executeKVCommand).isVisible());
        assertTrue(mainForm.getWidget(properties.executeJSONCommand).isVisible());
        assertTrue(mainForm.getWidget(properties.command).isVisible());
        assertTrue(mainForm.getWidget(properties.keyValueTable).isHidden());
        assertTrue(mainForm.getWidget(properties.jsonCommand).isHidden());
        assertTrue(mainForm.getWidget(properties.function).isHidden());
        assertTrue(mainForm.getWidget(properties.functionParams).isHidden());

        // Construct Command from KV
        properties.executeKVCommand.setValue(true);
        componentService.afterProperty(properties.executeKVCommand.getName(), properties);
        assertTrue(mainForm.getWidget(properties.executeCommand).isVisible());
        assertTrue(mainForm.getWidget(properties.executeJSONCommand).isHidden());
        assertTrue(mainForm.getWidget(properties.command).isHidden());
        assertTrue(mainForm.getWidget(properties.keyValueTable).isVisible());
        assertTrue(mainForm.getWidget(properties.jsonCommand).isHidden());
        assertTrue(mainForm.getWidget(properties.function).isHidden());
        assertTrue(mainForm.getWidget(properties.functionParams).isHidden());

        // Construct Command from json
        properties.executeKVCommand.setValue(false);
        componentService.afterProperty(properties.executeKVCommand.getName(), properties);
        properties.executeJSONCommand.setValue(true);
        componentService.afterProperty(properties.executeJSONCommand.getName(), properties);
        assertTrue(mainForm.getWidget(properties.executeCommand).isVisible());
        assertTrue(mainForm.getWidget(properties.executeKVCommand).isHidden());
        assertTrue(mainForm.getWidget(properties.command).isHidden());
        assertTrue(mainForm.getWidget(properties.keyValueTable).isHidden());
        assertTrue(mainForm.getWidget(properties.jsonCommand).isVisible());
        assertTrue(mainForm.getWidget(properties.function).isHidden());
        assertTrue(mainForm.getWidget(properties.functionParams).isHidden());

    }

    @Test
    public void testRefreshLayout() throws Exception {
        TMongoDBRowProperties properties = new TMongoDBRowProperties("properties");
        properties.init();

        ComponentTestUtils.checkSerialize(properties, errorCollector);
        Form mainForm = properties.getForm(Form.MAIN);
        assertTrue(mainForm.getWidget(properties.schema).isVisible());
        assertTrue(mainForm.getWidget(properties.executeCommand).isVisible());
        assertTrue(mainForm.getWidget(properties.executeKVCommand).isHidden());
        assertTrue(mainForm.getWidget(properties.executeJSONCommand).isHidden());
        assertTrue(mainForm.getWidget(properties.command).isHidden());
        assertTrue(mainForm.getWidget(properties.keyValueTable).isHidden());
        assertTrue(mainForm.getWidget(properties.jsonCommand).isHidden());
        assertTrue(mainForm.getWidget(properties.function).isVisible());
        assertTrue(mainForm.getWidget(properties.functionParams).isVisible());
        assertTrue(mainForm.getWidget(properties.dieOnError).isVisible());

        // Default value
        assertFalse(properties.executeCommand.getValue());
        assertFalse(properties.executeKVCommand.getValue());
        assertFalse(properties.executeJSONCommand.getValue());
        assertNull(properties.command.getValue());
        assertNull(properties.keyValueTable.key.getValue());
        assertNull(properties.keyValueTable.value.getValue());
        assertEquals("{createIndexes: 'restaurants', indexes : [{key : {restaurant_id: 1}, name: 'id_index_2', unique: true}]}",
                properties.jsonCommand.getValue());
        assertNull(properties.command.getValue());
        assertNull(properties.function.getValue());
        assertNull(properties.functionParams.parameter.getValue());
        assertFalse(properties.dieOnError.getValue());

    }

    @Test
    public void testGetAllSchemaPropertiesConnectors() throws Exception {
        TMongoDBRowProperties properties = new TMongoDBRowProperties("properties");
        properties.init();
        assertEquals(1, properties.getAllSchemaPropertiesConnectors(false).size());
        PropertyPathConnector mainConnector = (PropertyPathConnector) properties.getAllSchemaPropertiesConnectors(false)
                .toArray()[0];
        assertEquals(Connector.MAIN_NAME, mainConnector.getName());
        assertEquals("schema", mainConnector.getPropertyPath());
        assertEquals(0, properties.getAllSchemaPropertiesConnectors(true).size());
    }

}