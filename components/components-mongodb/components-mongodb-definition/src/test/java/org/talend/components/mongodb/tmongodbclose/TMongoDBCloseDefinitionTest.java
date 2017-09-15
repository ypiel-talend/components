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

package org.talend.components.mongodb.tmongodbclose;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.talend.components.api.component.ComponentDefinition.RETURN_ERROR_MESSAGE_PROP;
import static org.talend.components.mongodb.common.MongoDBDefinition.SOURCE_OR_SINK_CLASS;

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

public class TMongoDBCloseDefinitionTest extends MongoDBDefinitionTestBasic {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void testTMongoDBCloseDefinition() throws Exception {
        TMongoDBCloseDefinition definition = new TMongoDBCloseDefinition();
        testDefinition(definition);
        assertEquals(true, definition.isStartable());
        assertEquals(null, definition.getPartitioning());
        assertEquals(false, definition.isSchemaAutoPropagate());
    }

    @Test
    public void testGetFamilies() {
        testGetFamilies(new TMongoDBCloseDefinition());
    }

    @Test
    public void testGetPropertyClass() {
        TMongoDBCloseDefinition definition = new TMongoDBCloseDefinition();
        Class<?> propertyClass = definition.getPropertyClass();
        String canonicalName = propertyClass.getCanonicalName();

        assertThat(canonicalName, equalTo("org.talend.components.mongodb.tmongodbclose.TMongoDBCloseProperties"));
    }

    @Test
    public void testGetReturnProperties() {
        TMongoDBCloseDefinition definition = new TMongoDBCloseDefinition();
        Property[] returnProperties = definition.getReturnProperties();
        List<Property> propertyList = Arrays.asList(returnProperties);

        assertThat(propertyList, hasSize(1));
        assertTrue(propertyList.contains(RETURN_ERROR_MESSAGE_PROP));
    }

    @Test
    public void testGetRuntimeInfo() {
        TMongoDBCloseDefinition definition = new TMongoDBCloseDefinition();
        RuntimeInfo runtimeInfo = definition.getRuntimeInfo(ExecutionEngine.DI, null, ConnectorTopology.NONE);
        String runtimeClassName = runtimeInfo.getRuntimeClassName();
        assertThat(runtimeClassName, equalTo(SOURCE_OR_SINK_CLASS));
    }

    @Test
    public void testGetRuntimeInfoWrongEngine() {
        TMongoDBCloseDefinition definition = new TMongoDBCloseDefinition();
        thrown.expect(TalendRuntimeException.class);
        thrown.expectMessage("WRONG_EXECUTION_ENGINE:{component=tMongoDBClose, requested=DI_SPARK_STREAMING, available=[DI]}");
        definition.getRuntimeInfo(ExecutionEngine.DI_SPARK_STREAMING, null, ConnectorTopology.NONE);
    }

    @Test
    public void testGetRuntimeInfoWrongTopology() {
        TMongoDBCloseDefinition definition = new TMongoDBCloseDefinition();
        thrown.expect(TalendRuntimeException.class);
        thrown.expectMessage("WRONG_CONNECTOR:{component=tMongoDBClose}");
        definition.getRuntimeInfo(ExecutionEngine.DI, null, ConnectorTopology.INCOMING);
    }

    @Test
    public void testGetSupportedConnectorTopologies() {
        TMongoDBCloseDefinition definition = new TMongoDBCloseDefinition();
        Set<ConnectorTopology> connectorTopologies = definition.getSupportedConnectorTopologies();

        assertThat(connectorTopologies, contains(ConnectorTopology.NONE));
        assertThat(connectorTopologies,
                not((contains(ConnectorTopology.INCOMING, ConnectorTopology.OUTGOING, ConnectorTopology.INCOMING_AND_OUTGOING))));
    }

    @Test
    public void testGetNestedCompatibleComponentPropertiesClass() {
        TMongoDBCloseDefinition definition = new TMongoDBCloseDefinition();
        Class<? extends ComponentProperties>[] propertiesClass = definition.getNestedCompatibleComponentPropertiesClass();
        assertEquals(1, propertiesClass.length);
        assertTrue(propertiesClass[0].equals(MongoDBConnectionProperties.class));
    }

}