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

package org.talend.components.mongodb.runtime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.mongodb.tmongodbclose.TMongoDBCloseDefinition;
import org.talend.components.mongodb.tmongodbclose.TMongoDBCloseProperties;
import org.talend.daikon.properties.ValidationResult;

@Ignore
public class MongoDBCloseSourceOrSinkTest extends MongoDBTestBasic {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDBCloseSourceOrSinkTest.class);

    @Before
    @Override
    public void prepareTestData() {
        MongoDBSourceOrSink sourceOrSink = getInitializedSourceOrSink();
        // Specify the database name
        sourceOrSink.properties.database.setValue(DEFAULT_DB);
        ValidationResult resultOK = sourceOrSink.validate(getRuntimeContainer(CONNECTION_COMP_ID, false));
        LOGGER.debug(resultOK.getMessage());
        assertEquals(ValidationResult.Result.OK, resultOK.getStatus());
    }

    @Test
    public void testClose() throws Exception {
        MongoDBCloseSourceOrSink closeSourceOrSink = new MongoDBCloseSourceOrSink();
        TMongoDBCloseProperties properties = new TMongoDBCloseProperties("properties");
        properties.init();
        properties.referencedComponent.componentInstanceId.setValue(CONNECTION_COMP_ID);

        String currentCompId = TMongoDBCloseDefinition.COMPONENT_NAME + "_1";
        RuntimeContainer container = getRuntimeContainer(currentCompId, false);

        Object mongo = container.getComponentData(CONNECTION_COMP_ID, MongoDBSourceOrSink.KEY_MONGO);
        // Check whether the mongo instance exist in the globalMap
        assertNotNull(mongo);

        closeSourceOrSink.initialize(container, properties);
        ValidationResult result = closeSourceOrSink.validate(container);
        assertEquals(ValidationResult.Result.OK, result.getStatus());
    }

}