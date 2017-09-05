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

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.mongodb.MongoDBConnectionProperties;
import org.talend.components.mongodb.MongoDBTestBase;
import org.talend.daikon.properties.presentation.Form;

public class MongoDBConnectionPropertiesTest extends MongoDBTestBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDBConnectionPropertiesTest.class);

    /**
     * Checks forms are filled with required widgets
     */
    @Test
    public void testSetupLayout() {
        MongoDBConnectionProperties properties = new MongoDBConnectionProperties("root");
        properties.init();
        properties.setupLayout();

        Form main = properties.getForm(Form.MAIN);
        // ComponentTestUtils.checkSerialize(properties, errorCollector);
    }

    /**
     * Checks default values are set correctly
     */
    @Test
    public void testSetupProperties() {
        MongoDBConnectionProperties properties = new MongoDBConnectionProperties("root");
        properties.setupProperties();
        // TODO
    }

    /**
     * Checks initial layout
     */
    @Test
    public void testRefreshLayout() {
        MongoDBConnectionProperties properties = new MongoDBConnectionProperties("root");
        properties.init();

        properties.refreshLayout(properties.getForm(Form.MAIN));

        // TODO
    }
}
