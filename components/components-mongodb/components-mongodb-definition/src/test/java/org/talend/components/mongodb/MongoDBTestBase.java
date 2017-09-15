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
package org.talend.components.mongodb;

import javax.inject.Inject;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.junit.Rule;
import org.junit.rules.ErrorCollector;
import org.talend.components.api.service.ComponentService;
import org.talend.components.api.service.common.ComponentServiceImpl;
import org.talend.components.api.service.common.DefinitionRegistry;
import org.talend.components.api.test.AbstractComponentTest2;

public class MongoDBTestBase extends AbstractComponentTest2 {

    private ComponentServiceImpl componentService;

    @Rule
    public ErrorCollector errorCollector = new ErrorCollector();

    @Inject
    DefinitionRegistry definitionRegistry;

    @Override
    public DefinitionRegistry getDefinitionRegistry() {
        if (definitionRegistry == null) {
            definitionRegistry = new DefinitionRegistry();
            definitionRegistry.registerComponentFamilyDefinition(new MongoDBFamilyDefinition());
        }
        return definitionRegistry;
    }

    public ComponentService getComponentService() {
        if (componentService == null) {
            componentService = new ComponentServiceImpl(getDefinitionRegistry());
        }
        return componentService;
    }

}
