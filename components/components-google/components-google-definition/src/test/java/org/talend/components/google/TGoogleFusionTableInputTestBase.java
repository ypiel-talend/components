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
package org.talend.components.google;

import javax.inject.Inject;

import org.junit.Test;
import org.talend.components.api.component.ComponentDefinition;
import org.talend.components.api.test.AbstractComponentTest2;
import org.talend.components.google.tgooglefusiontableinput.TGoogleFusionTableInputDefinition;
import org.talend.daikon.definition.Definition;
import org.talend.daikon.definition.service.DefinitionRegistryService;




public class TGoogleFusionTableInputTestBase extends AbstractComponentTest2 {

    @Inject
    private DefinitionRegistryService definitionRegistry;

    @Override
    public DefinitionRegistryService getDefinitionRegistry() {
        return definitionRegistry;
    }
    
    @Test
    public void testComponentHasBeenRegistered(){

    	assertComponentIsRegistered(ComponentDefinition.class, "tGoogleFusionTableInput", TGoogleFusionTableInputDefinition.class);
        assertComponentIsRegistered(Definition.class, "tGoogleFusionTableInput", TGoogleFusionTableInputDefinition.class);
    }
}
