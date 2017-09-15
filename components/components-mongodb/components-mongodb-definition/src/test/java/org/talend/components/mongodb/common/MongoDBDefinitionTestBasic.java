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

package org.talend.components.mongodb.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

public class MongoDBDefinitionTestBasic {

    protected void testGetFamilies(MongoDBDefinition definition) {
        assertNotNull(definition);
        String[] actual = definition.getFamilies();
        assertEquals(2, actual.length);
        assertEquals("Databases/MongoDB", Arrays.asList(actual).get(0));
        assertEquals("Big Data/MongoDB", Arrays.asList(actual).get(1));
    }

    protected void testDefinition(MongoDBDefinition definition) {
        assertEquals(null, definition.getIconKey());
        assertEquals(false, definition.isConditionalInputs());
        assertEquals(false, definition.isDataAutoPropagate());
        assertEquals(false, definition.isRejectAfterClose());
        assertNotNull(definition.getDisplayName());
        assertNotNull(definition.getTitle());
    }

}