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
package org.talend.components.mongodb.integration;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.mongodb.MongoDBConnectionProperties;
import org.talend.daikon.properties.ValidationResult;

@Ignore
public class MongoDBConnectionTestIT extends MongoDBTestBasic {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDBConnectionTestIT.class);

    private static final String DEFAULT_DB = "testdb";

    @Test
    public void testValidateConnectionWrongHost() throws Throwable {
        MongoDBConnectionProperties properties = createConnectionProperties();
        properties.host.setValue("WRONG_HOST");

        ValidationResult result = properties.validateTestConnection();
        assertEquals(ValidationResult.Result.ERROR, result.getStatus());
        LOGGER.debug(result.getMessage());
    }

    @Test
    public void testValidateConnectionAuth() throws Throwable {
        MongoDBConnectionProperties properties = createConnectionProperties();
        properties.database.setValue(DEFAULT_DB);
        properties.requiredAuthentication.setValue(true);
        properties.validateTestConnection();

        ValidationResult result = properties.validateTestConnection();
        assertEquals(ValidationResult.Result.OK, result.getStatus());
        LOGGER.debug(result.getMessage());

    }
}
