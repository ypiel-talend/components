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
package org.talend.components.mongodb.tmongodbconnection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.talend.components.api.component.ComponentDefinition.RETURN_ERROR_MESSAGE_PROP;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.talend.components.api.component.ConnectorTopology;
import org.talend.components.api.component.runtime.ExecutionEngine;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.runtime.RuntimeInfo;

public class TMongoDBConnectionDefinitionTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void testGetFamilies() {
        TMongoDBConnectionDefinition definition = new TMongoDBConnectionDefinition();
        String[] actual = definition.getFamilies();
        assertEquals(2, actual.length);
        assertEquals("Databases/MongoDB", Arrays.asList(actual).get(0));
        assertEquals("Big Data/MongoDB", Arrays.asList(actual).get(1));
    }

    @Test
    public void testGetPropertyClass() {
        TMongoDBConnectionDefinition definition = new TMongoDBConnectionDefinition();
        Class<?> propertyClass = definition.getPropertyClass();
        String canonicalName = propertyClass.getCanonicalName();

        assertThat(canonicalName, equalTo("org.talend.components.mongodb.MongoDBConnectionProperties"));
    }

    @Test
    public void testGetReturnProperties() {
        TMongoDBConnectionDefinition definition = new TMongoDBConnectionDefinition();
        Property[] returnProperties = definition.getReturnProperties();
        List<Property> propertyList = Arrays.asList(returnProperties);

        assertThat(propertyList, hasSize(1));
        assertTrue(propertyList.contains(RETURN_ERROR_MESSAGE_PROP));
    }

    @Test
    public void testGetRuntimeInfo() {
        TMongoDBConnectionDefinition definition = new TMongoDBConnectionDefinition();
        RuntimeInfo runtimeInfo = definition.getRuntimeInfo(ExecutionEngine.DI, null, ConnectorTopology.NONE);
        String runtimeClassName = runtimeInfo.getRuntimeClassName();
        assertThat(runtimeClassName, equalTo("org.talend.components.mongodb.runtime.MongoDBSourceOrSink"));
    }

    @Test
    public void testGetRuntimeInfoWrongEngine() {
        TMongoDBConnectionDefinition definition = new TMongoDBConnectionDefinition();
        thrown.expect(TalendRuntimeException.class);
        thrown.expectMessage(
                "WRONG_EXECUTION_ENGINE:{component=tMongoDBConnection, requested=DI_SPARK_STREAMING, available=[DI, BEAM]}");
        definition.getRuntimeInfo(ExecutionEngine.DI_SPARK_STREAMING, null, ConnectorTopology.NONE);
    }

    @Test
    public void testGetRuntimeInfoWrongTopology() {
        TMongoDBConnectionDefinition definition = new TMongoDBConnectionDefinition();
        thrown.expect(TalendRuntimeException.class);
        thrown.expectMessage("WRONG_CONNECTOR:{component=tMongoDBConnection}");
        definition.getRuntimeInfo(ExecutionEngine.DI, null, ConnectorTopology.INCOMING);
    }

    @Test
    public void testGetSupportedConnectorTopologies() {
        TMongoDBConnectionDefinition definition = new TMongoDBConnectionDefinition();
        Set<ConnectorTopology> connectorTopologies = definition.getSupportedConnectorTopologies();

        assertThat(connectorTopologies, contains(ConnectorTopology.NONE));
        assertThat(connectorTopologies,
                not((contains(ConnectorTopology.INCOMING, ConnectorTopology.OUTGOING, ConnectorTopology.INCOMING_AND_OUTGOING))));
    }

}