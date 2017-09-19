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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.talend.components.api.component.AbstractComponentDefinition.NONE;
import static org.talend.components.api.component.ComponentDefinition.RETURN_ERROR_MESSAGE_PROP;
import static org.talend.components.api.component.ComponentDefinition.RETURN_TOTAL_RECORD_COUNT_PROP;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.talend.components.api.component.ConnectorTopology;
import org.talend.components.api.component.runtime.ExecutionEngine;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.mongodb.MongoDBCollectionProperties;
import org.talend.components.mongodb.MongoDBConnectionProperties;
import org.talend.components.mongodb.common.MongoDBDefinition;
import org.talend.components.mongodb.common.MongoDBDefinitionTestBasic;
import org.talend.components.mongodb.tmongodbinput.TMongoDBInputDefinition;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.runtime.RuntimeInfo;

public class TMongoDBOutputDefinitionTest extends MongoDBDefinitionTestBasic {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void testGetFamilies() {
        testGetFamilies(new TMongoDBOutputDefinition());
    }

    @Test
    public void testTMongoDBDBOutputDefinition() throws Exception {
        TMongoDBOutputDefinition definition = new TMongoDBOutputDefinition();
        testDefinition(definition);
        assertEquals(false, definition.isStartable());
        assertEquals(NONE, definition.getPartitioning());
        assertEquals(true, definition.isSchemaAutoPropagate());
    }

    @Test
    public void testGetPropertyClass() {
        TMongoDBOutputDefinition definition = new TMongoDBOutputDefinition();
        Class<?> propertyClass = definition.getPropertyClass();
        String canonicalName = propertyClass.getCanonicalName();

        assertThat(canonicalName, equalTo("org.talend.components.mongodb.tmongodboutput.TMongoDBOutputProperties"));
    }

    @Test
    public void testGetReturnProperties() {
        TMongoDBOutputDefinition definition = new TMongoDBOutputDefinition();
        Property[] returnProperties = definition.getReturnProperties();
        List<Property> propertyList = Arrays.asList(returnProperties);

        assertThat(propertyList, hasSize(2));
        assertTrue(propertyList.contains(RETURN_ERROR_MESSAGE_PROP));
        assertTrue(propertyList.contains(RETURN_TOTAL_RECORD_COUNT_PROP));
    }

    @Test
    public void testGetRuntimeInfo() {
        TMongoDBOutputDefinition definition = new TMongoDBOutputDefinition();
        RuntimeInfo runtimeInfo = definition.getRuntimeInfo(ExecutionEngine.DI, null, ConnectorTopology.INCOMING);
        String runtimeClassName = runtimeInfo.getRuntimeClassName();
        assertThat(runtimeClassName, equalTo(MongoDBDefinition.SINK_CLASS));
    }

    @Test
    public void testGetSupportedConnectorTopologies() {
        TMongoDBOutputDefinition definition = new TMongoDBOutputDefinition();
        Set<ConnectorTopology> connectorTopologies = definition.getSupportedConnectorTopologies();

        assertThat(connectorTopologies, containsInAnyOrder(ConnectorTopology.INCOMING, ConnectorTopology.INCOMING_AND_OUTGOING));
        assertThat(connectorTopologies, not((contains(ConnectorTopology.NONE, ConnectorTopology.OUTGOING))));
    }

    @Test
    public void testGetRuntimeInfoWrongEngine() {
        TMongoDBOutputDefinition definition = new TMongoDBOutputDefinition();
        thrown.expect(TalendRuntimeException.class);
        thrown.expectMessage("WRONG_EXECUTION_ENGINE:{component=tMongoDBOutput, requested=DI_SPARK_STREAMING, available=[DI]}");
        definition.getRuntimeInfo(ExecutionEngine.DI_SPARK_STREAMING, null, ConnectorTopology.NONE);
    }

    @Test
    public void testGetRuntimeInfoWrongTopology() {
        TMongoDBOutputDefinition definition = new TMongoDBOutputDefinition();
        thrown.expect(TalendRuntimeException.class);
        thrown.expectMessage("WRONG_CONNECTOR:{component=tMongoDBOutput}");
        definition.getRuntimeInfo(ExecutionEngine.DI, null, ConnectorTopology.NONE);
    }

    @Test
    public void testGetNestedCompatibleComponentPropertiesClass() {
        TMongoDBOutputDefinition definition = new TMongoDBOutputDefinition();
        Class<? extends ComponentProperties>[] propertiesClass = definition.getNestedCompatibleComponentPropertiesClass();
        assertEquals(2, propertiesClass.length);
        assertTrue(propertiesClass[0].equals(MongoDBConnectionProperties.class));
        assertTrue(propertiesClass[1].equals(MongoDBCollectionProperties.class));
    }

}