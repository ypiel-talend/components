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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.talend.components.api.component.AbstractComponentDefinition.NONE;
import static org.talend.components.api.component.ComponentDefinition.RETURN_ERROR_MESSAGE_PROP;
import static org.talend.components.mongodb.common.MongoDBDefinition.ROW_SINK_CLASS;
import static org.talend.components.mongodb.common.MongoDBDefinition.ROW_SINK_OR_CLASS;

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

public class TMongoDBRowDefinitionTest extends MongoDBDefinitionTestBasic {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void testTMongoDBRowDefinition() throws Exception {
        TMongoDBRowDefinition definition = new TMongoDBRowDefinition();
        testDefinition(definition);
        assertEquals(true, definition.isStartable());
        assertEquals(NONE, definition.getPartitioning());
        assertEquals(true, definition.isSchemaAutoPropagate());
    }

    @Test
    public void testGetFamilies() {
        testGetFamilies(new TMongoDBRowDefinition());
    }

    @Test
    public void testGetPropertyClass() {
        TMongoDBRowDefinition definition = new TMongoDBRowDefinition();
        Class<?> propertyClass = definition.getPropertyClass();
        String canonicalName = propertyClass.getCanonicalName();

        assertThat(canonicalName, equalTo("org.talend.components.mongodb.tmongodbrow.TMongoDBRowProperties"));
    }

    @Test
    public void testGetReturnProperties() {
        TMongoDBRowDefinition definition = new TMongoDBRowDefinition();
        Property[] returnProperties = definition.getReturnProperties();
        List<Property> propertyList = Arrays.asList(returnProperties);

        assertThat(propertyList, hasSize(1));
        assertTrue(propertyList.contains(RETURN_ERROR_MESSAGE_PROP));
    }

    @Test
    public void testGetRuntimeInfo() {
        TMongoDBRowDefinition definition = new TMongoDBRowDefinition();
        RuntimeInfo runtimeIn = definition.getRuntimeInfo(ExecutionEngine.DI, null, ConnectorTopology.INCOMING);

        RuntimeInfo runtimeNone = definition.getRuntimeInfo(ExecutionEngine.DI, null, ConnectorTopology.NONE);
        assertThat(runtimeIn.getRuntimeClassName(), equalTo(ROW_SINK_CLASS));
        assertThat(runtimeNone.getRuntimeClassName(), equalTo(ROW_SINK_OR_CLASS));
    }

    @Test
    public void testGetRuntimeInfoWrongEngine() {
        TMongoDBRowDefinition definition = new TMongoDBRowDefinition();
        thrown.expect(TalendRuntimeException.class);
        thrown.expectMessage("WRONG_EXECUTION_ENGINE:{component=tMongoDBRow, requested=DI_SPARK_STREAMING, available=[DI]}");
        definition.getRuntimeInfo(ExecutionEngine.DI_SPARK_STREAMING, null, ConnectorTopology.NONE);
    }

    @Test
    public void testGetRuntimeInfoWrongTopology() {
        TMongoDBRowDefinition definition = new TMongoDBRowDefinition();
        thrown.expect(TalendRuntimeException.class);
        thrown.expectMessage("WRONG_CONNECTOR:{component=tMongoDBRow}");
        definition.getRuntimeInfo(ExecutionEngine.DI, null, ConnectorTopology.OUTGOING);
    }

    @Test
    public void testGetSupportedConnectorTopologies() {
        TMongoDBRowDefinition definition = new TMongoDBRowDefinition();
        Set<ConnectorTopology> connectorTopologies = definition.getSupportedConnectorTopologies();

        assertThat(connectorTopologies, containsInAnyOrder(ConnectorTopology.INCOMING, ConnectorTopology.NONE));
        assertThat(connectorTopologies, not((contains(ConnectorTopology.OUTGOING, ConnectorTopology.INCOMING_AND_OUTGOING))));
    }

    @Test
    public void testGetNestedCompatibleComponentPropertiesClass() {
        TMongoDBRowDefinition definition = new TMongoDBRowDefinition();
        Class<? extends ComponentProperties>[] propertiesClass = definition.getNestedCompatibleComponentPropertiesClass();
        assertEquals(1, propertiesClass.length);
        assertTrue(propertiesClass[0].equals(MongoDBConnectionProperties.class));
    }
}