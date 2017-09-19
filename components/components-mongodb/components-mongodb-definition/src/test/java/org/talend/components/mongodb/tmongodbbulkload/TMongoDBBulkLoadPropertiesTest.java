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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;
import org.talend.components.api.service.ComponentService;
import org.talend.components.api.test.ComponentTestUtils;
import org.talend.components.mongodb.MongoDBConnectionProperties;
import org.talend.components.mongodb.MongoDBTestBase;
import org.talend.daikon.properties.presentation.Form;

public class TMongoDBBulkLoadPropertiesTest extends MongoDBTestBase {

    @Test
    public void testSetupLayout() throws Exception {
        TMongoDBBulkLoadProperties properties = new TMongoDBBulkLoadProperties("properties");
        properties.init();

        ComponentTestUtils.checkSerialize(properties, errorCollector);
        Form mainForm = properties.getForm(Form.MAIN);
        assertTrue(mainForm.getWidget(properties.mongoDBHome).isVisible());
        assertTrue(mainForm.getWidget(properties.useLocalDBPath).isVisible());
        assertTrue(mainForm.getWidget(properties.localDBPath).isHidden());
        assertTrue(mainForm.getWidget(properties.specifyReplicateSet).isHidden());
        assertTrue(mainForm.getWidget(properties.replicateName).isHidden());
        assertTrue(mainForm.getWidget(properties.collection).isVisible());
        assertTrue(mainForm.getWidget(properties.dropExistCollection).isVisible());
        assertTrue(mainForm.getWidget(properties.dataAction).isVisible());
        assertTrue(mainForm.getWidget(properties.upsertField).isHidden());
        assertTrue(mainForm.getWidget(properties.fileType).isVisible());
        assertTrue(mainForm.getWidget(properties.headerLine).isVisible());
        assertTrue(mainForm.getWidget(properties.ignoreBlanks).isVisible());
        assertTrue(mainForm.getWidget(properties.jsonArray).isHidden());
        assertTrue(mainForm.getWidget(properties.printLog).isVisible());

        Form advancedForm = properties.getForm(Form.ADVANCED);
        assertTrue(advancedForm.getWidget(properties.additionalArgs).isVisible());

        // Default value
        assertNull(properties.mongoDBHome.getValue());
        assertFalse(properties.useLocalDBPath.getValue());
        assertNull(properties.localDBPath.getValue());
        assertFalse(properties.specifyReplicateSet.getValue());
        assertNull(properties.replicateName.getValue());
        assertNull(properties.collection.collectionName.getValue());
        assertFalse(properties.dropExistCollection.getValue());
        assertEquals(TMongoDBBulkLoadProperties.DataAction.INSERT, properties.dataAction.getValue());
        assertTrue(properties.dataAction.getPossibleValues()
                .containsAll(Arrays.asList(TMongoDBBulkLoadProperties.DataAction.values())));
        assertNull(properties.upsertField.columnName.getValue());
        assertNull(properties.dataFile.getValue());
        assertEquals(TMongoDBBulkLoadProperties.FileType.csv, properties.fileType.getValue());
        assertTrue(
                properties.fileType.getPossibleValues().containsAll(Arrays.asList(TMongoDBBulkLoadProperties.FileType.values())));
        assertFalse(properties.headerLine.getValue());
        assertFalse(properties.ignoreBlanks.getValue());
        assertFalse(properties.jsonArray.getValue());
        assertFalse(properties.printLog.getValue());

    }

    @Test
    public void testRefreshLayout() throws Throwable {
        TMongoDBBulkLoadProperties properties = new TMongoDBBulkLoadProperties("properties");
        properties.init();

        Form mainForm = properties.getForm(Form.MAIN);
        Form advanceForm = properties.getForm(Form.ADVANCED);
        Form connForm = mainForm.getChildForm(properties.connection.getName());
        ComponentService componentService = getComponentService();

        // UseLocalDBPath
        properties.useLocalDBPath.setValue(true);
        componentService.afterProperty(properties.useLocalDBPath.getName(), properties);
        assertTrue(connForm.getWidget(properties.connection.host).isHidden());
        assertTrue(connForm.getWidget(properties.connection.port).isHidden());
        assertTrue(connForm.getWidget(properties.connection.dbVersion).isHidden());
        assertTrue(connForm.getWidget(properties.connection.useReplicaSet).isHidden());
        assertTrue(connForm.getWidget(properties.connection.useSSL).isHidden());
        assertTrue(connForm.getWidget(properties.connection.replicaSetTable).isHidden());
        assertTrue(mainForm.getWidget(properties.specifyReplicateSet).isHidden());
        assertTrue(mainForm.getWidget(properties.replicateName).isHidden());

        // UseReplica
        properties.useLocalDBPath.setValue(false);
        componentService.afterProperty(properties.useLocalDBPath.getName(), properties);
        assertTrue(connForm.getWidget(properties.connection.useReplicaSet).isVisible());
        properties.connection.useReplicaSet.setValue(true);
        componentService.afterProperty(properties.connection.useReplicaSet.getName(), properties.connection);
        properties.refreshLayout(mainForm);
        assertTrue(connForm.getWidget(properties.connection.host).isHidden());
        assertTrue(connForm.getWidget(properties.connection.port).isHidden());
        assertTrue(connForm.getWidget(properties.connection.dbVersion).isHidden());
        assertTrue(connForm.getWidget(properties.connection.useSSL).isVisible());
        assertTrue(connForm.getWidget(properties.connection.replicaSetTable).isVisible());
        assertTrue(mainForm.getWidget(properties.specifyReplicateSet).isVisible());
        assertTrue(mainForm.getWidget(properties.replicateName).isHidden());

        // Specify replicateset
        properties.specifyReplicateSet.setValue(true);
        componentService.afterProperty(properties.specifyReplicateSet.getName(), properties);
        assertTrue(mainForm.getWidget(properties.replicateName).isVisible());

        // DataAction
        properties.dataAction.setValue(TMongoDBBulkLoadProperties.DataAction.UPSERT);
        componentService.afterProperty(properties.dataAction.getName(), properties);
        assertTrue(mainForm.getWidget(properties.upsertField).isVisible());

        // FileType
        properties.fileType.setValue(TMongoDBBulkLoadProperties.FileType.json);
        componentService.afterProperty(properties.fileType.getName(), properties);
        assertTrue(mainForm.getWidget(properties.headerLine).isHidden());
        assertTrue(mainForm.getWidget(properties.ignoreBlanks).isHidden());
        assertTrue(mainForm.getWidget(properties.jsonArray).isVisible());
        properties.fileType.setValue(TMongoDBBulkLoadProperties.FileType.tsv);
        componentService.afterProperty(properties.fileType.getName(), properties);
        assertTrue(mainForm.getWidget(properties.headerLine).isVisible());
        assertTrue(mainForm.getWidget(properties.ignoreBlanks).isVisible());
        assertTrue(mainForm.getWidget(properties.jsonArray).isHidden());

    }

    @Test
    public void testGetConnectionProperties() throws Exception {
        TMongoDBBulkLoadProperties properties = new TMongoDBBulkLoadProperties("properties");
        properties.init();
        MongoDBConnectionProperties connection = properties.getConnectionProperties();
        assertNotNull(connection);
        assertEquals("connection", connection.getName());

    }

}