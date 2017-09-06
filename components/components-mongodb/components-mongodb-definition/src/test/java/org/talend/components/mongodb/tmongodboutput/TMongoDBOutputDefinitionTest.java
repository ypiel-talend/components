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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.talend.components.api.component.ComponentDefinition.RETURN_ERROR_MESSAGE_PROP;
import static org.talend.components.api.component.ComponentDefinition.RETURN_TOTAL_RECORD_COUNT_PROP;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.talend.components.api.component.ConnectorTopology;
import org.talend.components.api.component.runtime.ExecutionEngine;
import org.talend.components.mongodb.common.MongoDBDefinition;
import org.talend.components.mongodb.tmongodbconnection.TMongoDBConnectionDefinition;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.runtime.RuntimeInfo;

public class TMongoDBOutputDefinitionTest {

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

        assertThat(connectorTopologies, contains(ConnectorTopology.INCOMING, ConnectorTopology.INCOMING_AND_OUTGOING));
        assertThat(connectorTopologies, not((contains(ConnectorTopology.NONE, ConnectorTopology.OUTGOING))));
    }

    @Test
    public void testOtherMethod() {
        TMongoDBOutputDefinition definition = new TMongoDBOutputDefinition();
        assertFalse(definition.isStartable());
    }
}