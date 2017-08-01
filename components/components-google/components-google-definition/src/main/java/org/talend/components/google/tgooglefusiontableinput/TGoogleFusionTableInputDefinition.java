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
package org.talend.components.google.tgooglefusiontableinput;

import java.util.EnumSet;
import java.util.Set;

import org.talend.components.api.component.ConnectorTopology;
import org.talend.components.api.component.runtime.ExecutionEngine;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.google.GoogleFusionTableDefinition;
import org.talend.components.google.RuntimeInfoProvider;
import org.talend.daikon.runtime.RuntimeInfo;

/**
 * The TGoogleFusionTableInputDefinition acts as an entry point for all of services that
 * a component provides to integrate with the Runtime Platform (at design-time) and other
 * components (at run-time).
 */
public class TGoogleFusionTableInputDefinition extends GoogleFusionTableDefinition {

    public static final String COMPONENT_NAME = "tGoogleFusionTableInput"; //$NON-NLS-1$

    public TGoogleFusionTableInputDefinition() {
        super(COMPONENT_NAME);
    }

    @Override
    public Class<? extends ComponentProperties> getPropertyClass() {
        return TGoogleFusionTableInputProperties.class;
    }

    @Override
    public RuntimeInfo getRuntimeInfo(ExecutionEngine engine, ComponentProperties properties,
            ConnectorTopology connectorTopology) {
        assertEngineCompatibility(engine);
        assertConnectorTopologyCompatibility(connectorTopology);
        return RuntimeInfoProvider.provideInputRuntimeInfo();
    }

    @Override
    public Set<ConnectorTopology> getSupportedConnectorTopologies() {
        return EnumSet.of(ConnectorTopology.OUTGOING);
    }

}
