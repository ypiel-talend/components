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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.ComponentFamilyDefinition;
import org.talend.components.api.ComponentInstaller;
import org.talend.components.api.wizard.ComponentWizardDefinition;
import org.talend.daikon.definition.Definition;

public class MongoDBFamilyDefinitionTest extends MongoDBTestBase {

    private transient static final Logger LOG = LoggerFactory.getLogger(MongoDBFamilyDefinitionTest.class);

    @Test
    public void testComponentRegiester() throws Exception {

        assertNotNull(getDefinitionRegistry());
        assertEquals(8, definitionRegistry.getDefinitions().size());

    }

    @Test
    public void testInstall() throws Exception {
        final MongoDBFamilyDefinition def = new MongoDBFamilyDefinition();
        ComponentInstaller.ComponentFrameworkContext ctx = new ComponentInstaller.ComponentFrameworkContext() {

            @Override
            public void registerComponentFamilyDefinition(ComponentFamilyDefinition def) {
                LOG.debug("def = [" + def + "]");
                assertEquals("MongoDB", def.getName());
            }

            @Override
            public void registerDefinition(Iterable<? extends Definition> defs) {
                LOG.debug("defs = [" + defs + "]");
                assertNull(defs);
            }

            @Override
            public void registerComponentWizardDefinition(Iterable<? extends ComponentWizardDefinition> defs) {
                LOG.debug("defs = [" + defs + "]");
                assertNull(def);
            }
        };
        def.install(ctx);
    }

}