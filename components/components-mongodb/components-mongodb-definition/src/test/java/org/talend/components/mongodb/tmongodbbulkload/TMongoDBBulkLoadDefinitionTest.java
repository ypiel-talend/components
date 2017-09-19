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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.talend.components.api.component.AbstractComponentDefinition.NONE;
import static org.talend.components.api.component.ComponentDefinition.RETURN_ERROR_MESSAGE_PROP;
import static org.talend.components.mongodb.common.MongoDBDefinition.BULK_LOAD_RUNTIME_CLASS;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.talend.components.api.component.ConnectorTopology;
import org.talend.components.api.component.runtime.ExecutionEngine;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.mongodb.MongoDBConnectionProperties;
import org.talend.components.mongodb.common.MongoDBDefinitionTestBasic;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.runtime.RuntimeInfo;

public class TMongoDBBulkLoadDefinitionTest extends MongoDBDefinitionTestBasic {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void testTMongoDBBulkLoadDefinition() throws Exception {
        TMongoDBBulkLoadDefinition definition = new TMongoDBBulkLoadDefinition();
        testDefinition(definition);
        assertEquals(true, definition.isStartable());
        assertEquals(NONE, definition.getPartitioning());
        assertEquals(false, definition.isSchemaAutoPropagate());
    }

    @Test
    public void testGetFamilies() {
        testGetFamilies(new TMongoDBBulkLoadDefinition());
    }

    @Test
    public void testGetPropertyClass() {
        TMongoDBBulkLoadDefinition definition = new TMongoDBBulkLoadDefinition();
        Class<?> propertyClass = definition.getPropertyClass();
        String canonicalName = propertyClass.getCanonicalName();

        assertThat(canonicalName, equalTo("org.talend.components.mongodb.tmongodbbulkload.TMongoDBBulkLoadProperties"));
    }

    @Test
    public void testGetReturnProperties() {
        TMongoDBBulkLoadDefinition definition = new TMongoDBBulkLoadDefinition();
        Property[] returnProperties = definition.getReturnProperties();
        List<Property> propertyList = Arrays.asList(returnProperties);

        assertThat(propertyList, hasSize(1));
        assertTrue(propertyList.contains(RETURN_ERROR_MESSAGE_PROP));
    }

    @Test
    public void testGetRuntimeInfo() {
        TMongoDBBulkLoadDefinition definition = new TMongoDBBulkLoadDefinition();
        RuntimeInfo runtimeInfo = definition.getRuntimeInfo(ExecutionEngine.DI, null, ConnectorTopology.NONE);
        String runtimeClassName = runtimeInfo.getRuntimeClassName();
        assertThat(runtimeClassName, equalTo(BULK_LOAD_RUNTIME_CLASS));
    }

    @Test
    public void testGetRuntimeInfoWrongEngine() {
        TMongoDBBulkLoadDefinition definition = new TMongoDBBulkLoadDefinition();
        thrown.expect(TalendRuntimeException.class);
        thrown.expectMessage("WRONG_EXECUTION_ENGINE:{component=tMongoDBBulkLoad, requested=DI_SPARK_STREAMING, available=[DI]}");
        definition.getRuntimeInfo(ExecutionEngine.DI_SPARK_STREAMING, null, ConnectorTopology.NONE);
    }

    @Test
    public void testGetRuntimeInfoWrongTopology() {
        TMongoDBBulkLoadDefinition definition = new TMongoDBBulkLoadDefinition();
        thrown.expect(TalendRuntimeException.class);
        thrown.expectMessage("WRONG_CONNECTOR:{component=tMongoDBBulkLoad}");
        definition.getRuntimeInfo(ExecutionEngine.DI, null, ConnectorTopology.INCOMING);
    }

    @Test
    public void testGetSupportedConnectorTopologies() {
        TMongoDBBulkLoadDefinition definition = new TMongoDBBulkLoadDefinition();
        Set<ConnectorTopology> connectorTopologies = definition.getSupportedConnectorTopologies();

        assertThat(connectorTopologies, contains(ConnectorTopology.NONE));
        assertThat(connectorTopologies,
                not((contains(ConnectorTopology.INCOMING, ConnectorTopology.OUTGOING, ConnectorTopology.INCOMING_AND_OUTGOING))));
    }

    @Test
    public void testGetNestedCompatibleComponentPropertiesClass() {
        TMongoDBBulkLoadDefinition definition = new TMongoDBBulkLoadDefinition();
        Class<? extends ComponentProperties>[] propertiesClass = definition.getNestedCompatibleComponentPropertiesClass();
        assertEquals(1, propertiesClass.length);
        assertTrue(propertiesClass[0].equals(MongoDBConnectionProperties.class));
    }
}