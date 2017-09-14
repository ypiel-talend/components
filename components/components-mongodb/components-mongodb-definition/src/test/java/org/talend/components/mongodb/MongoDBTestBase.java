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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.talend.components.api.component.ComponentDefinition;
import org.talend.components.api.service.common.DefinitionRegistry;
import org.talend.components.api.test.AbstractComponentTest2;
import org.talend.components.mongodb.tmongodbconnection.TMongoDBConnectionDefinition;
import org.talend.daikon.definition.Definition;
import org.talend.daikon.definition.service.DefinitionRegistryService;

public class MongoDBTestBase extends AbstractComponentTest2 {

    @Rule
    public ErrorCollector errorCollector = new ErrorCollector();

    @Inject
    private DefinitionRegistry definitionRegistry;

    @Override
    public DefinitionRegistryService getDefinitionRegistry() {
        if (definitionRegistry == null) {
            definitionRegistry = new DefinitionRegistry();
            definitionRegistry.registerComponentFamilyDefinition(new MongoDBFamilyDefinition());
        }
        return definitionRegistry;
    }

    @Test
    public void testComponentHasBeenRegistered() {
        assertComponentIsRegistered(ComponentDefinition.class, "tMongoDBConnection", TMongoDBConnectionDefinition.class);
        assertComponentIsRegistered(Definition.class, "tMongoDBConnection", TMongoDBConnectionDefinition.class);
    }
}
