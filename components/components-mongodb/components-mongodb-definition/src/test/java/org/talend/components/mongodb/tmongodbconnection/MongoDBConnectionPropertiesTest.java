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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.talend.components.mongodb.MongoDBConnectionProperties.FORM_WIZARD;

import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.service.ComponentService;
import org.talend.components.api.test.ComponentTestUtils;
import org.talend.components.mongodb.MongoDBConnectionProperties;
import org.talend.components.mongodb.MongoDBTestBase;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.presentation.Form;

public class MongoDBConnectionPropertiesTest extends MongoDBTestBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDBConnectionPropertiesTest.class);

    /**
     * Checks forms are filled with required widgets
     */
    @Test
    public void testSetupLayout() {
        MongoDBConnectionProperties properties = new MongoDBConnectionProperties("properties");
        properties.init();

        ComponentTestUtils.checkSerialize(properties, errorCollector);

        // Main form default show status
        Form mainForm = properties.getForm(Form.MAIN);
        checkBasicSetting(mainForm, properties);
        assertTrue(mainForm.getWidget(properties.dbVersion).isVisible());
        assertTrue(mainForm.getWidget(properties.useReplicaSet).isVisible());
        assertTrue(mainForm.getWidget(properties.replicaSetTable).isHidden());
        assertTrue(mainForm.getWidget(properties.host).isVisible());
        assertTrue(mainForm.getWidget(properties.port).isVisible());
        assertTrue(mainForm.getWidget(properties.database).isVisible());
        assertTrue(mainForm.getWidget(properties.useSSL).isVisible());

        // Advanced form default show status
        Form advanceForm = properties.getForm(Form.ADVANCED);
        assertTrue(advanceForm.getWidget(properties.queryOptionNoTimeout).isVisible());
        // Wizard form default show status
        Form wizardForm = properties.getForm(FORM_WIZARD);
        checkBasicSetting(wizardForm, properties);
        assertTrue(wizardForm.getWidget(properties.testConnection).isVisible());

        // Default value
        assertTrue(properties.dbVersion.getPossibleValues()
                .containsAll(Arrays.asList(MongoDBConnectionProperties.DBVersion.values())));
        assertFalse(properties.useReplicaSet.getValue());
        assertEquals(MongoDBConnectionProperties.DBVersion.MONGODB_3_2_X, properties.dbVersion.getValue());
        assertNull(properties.replicaSetTable.host.getValue());
        assertNull(properties.replicaSetTable.port.getValue());
        assertNull(properties.host.getValue());
        assertThat(27017, is(properties.port.getValue()));
        assertNull(properties.database.getValue());
        assertFalse(properties.useSSL.getValue());
        assertFalse(properties.requiredAuthentication.getValue());
        List<MongoDBConnectionProperties.AuthenticationMechanism> allAuthTypes = Arrays.asList( //
                MongoDBConnectionProperties.AuthenticationMechanism.NEGOTIATE_MEC, //
                MongoDBConnectionProperties.AuthenticationMechanism.PLAIN_MEC, //
                MongoDBConnectionProperties.AuthenticationMechanism.SCRAMSHA1_MEC, //
                MongoDBConnectionProperties.AuthenticationMechanism.KERBEROS_MEC);
        assertTrue(properties.authenticationMechanism.getPossibleValues().containsAll(allAuthTypes));
        assertEquals(MongoDBConnectionProperties.AuthenticationMechanism.NEGOTIATE_MEC,
                properties.authenticationMechanism.getValue());
        assertFalse(properties.setAuthenticationDatabase.getValue());
        assertNull(properties.authenticationDatabase.getValue());

        assertNull(properties.userPassword.userId.getValue());
        assertNull(properties.userPassword.password.getValue());
        assertEquals("user@EXAMPLE.COM", properties.kerberos.userPrincipal.getValue());
        assertEquals("EXAMPLE.COM", properties.kerberos.realm.getValue());
        assertEquals("kdc.example.com", properties.kerberos.kdcServer.getValue());
        assertFalse(properties.queryOptionNoTimeout.getValue());

    }

    /**
     * Checks refresh layout
     */
    @Test
    public void testRefreshLayout() throws Throwable {
        MongoDBConnectionProperties properties = new MongoDBConnectionProperties("root");
        properties.init();

        Form mainForm = properties.getForm(Form.MAIN);
        ComponentService componentService = getComponentService();
        // Required authentication
        properties.requiredAuthentication.setValue(true);
        componentService.afterProperty(properties.requiredAuthentication.getName(), properties);
        assertTrue(mainForm.getWidget(properties.authenticationMechanism).isVisible());
        assertTrue(mainForm.getWidget(properties.setAuthenticationDatabase).isVisible());
        assertTrue(mainForm.getWidget(properties.authenticationDatabase).isHidden());
        assertTrue(mainForm.getWidget(properties.userPassword).isVisible());
        assertTrue(mainForm.getWidget(properties.kerberos).isHidden());
        // Set auth database
        properties.setAuthenticationDatabase.setValue(true);
        componentService.afterProperty(properties.setAuthenticationDatabase.getName(), properties);
        assertTrue(mainForm.getWidget(properties.authenticationDatabase).isVisible());
        // Change auth type
        properties.authenticationMechanism.setValue(MongoDBConnectionProperties.AuthenticationMechanism.PLAIN_MEC);
        componentService.afterProperty(properties.authenticationMechanism.getName(), properties);
        assertTrue(mainForm.getWidget(properties.setAuthenticationDatabase).isHidden());
        assertTrue(mainForm.getWidget(properties.authenticationDatabase).isHidden());
        assertTrue(mainForm.getWidget(properties.kerberos).isHidden());
        properties.authenticationMechanism.setValue(MongoDBConnectionProperties.AuthenticationMechanism.KERBEROS_MEC);
        componentService.afterProperty(properties.authenticationMechanism.getName(), properties);
        assertTrue(mainForm.getWidget(properties.setAuthenticationDatabase).isHidden());
        assertTrue(mainForm.getWidget(properties.authenticationDatabase).isHidden());
        assertTrue(mainForm.getWidget(properties.kerberos).isVisible());
        properties.authenticationMechanism.setValue(MongoDBConnectionProperties.AuthenticationMechanism.SCRAMSHA1_MEC);
        componentService.afterProperty(properties.authenticationMechanism.getName(), properties);
        assertTrue(mainForm.getWidget(properties.setAuthenticationDatabase).isVisible());
        assertTrue(mainForm.getWidget(properties.authenticationDatabase).isVisible());
        assertTrue(mainForm.getWidget(properties.kerberos).isHidden());

        // DBVersion changed
        properties.dbVersion.setValue(MongoDBConnectionProperties.DBVersion.MONGODB_2_5_X);
        componentService.afterProperty(properties.dbVersion.getName(), properties);
        assertEquals(MongoDBConnectionProperties.AuthenticationMechanism.NEGOTIATE_MEC,
                properties.authenticationMechanism.getValue());
        assertTrue(properties.authenticationMechanism.getPossibleValues()
                .containsAll(Arrays.asList(MongoDBConnectionProperties.AuthenticationMechanism.NEGOTIATE_MEC,
                        MongoDBConnectionProperties.AuthenticationMechanism.KERBEROS_MEC)));
        assertEquals(2, properties.authenticationMechanism.getPossibleValues().size());
        properties.dbVersion.setValue(MongoDBConnectionProperties.DBVersion.MONGODB_2_6_X);
        componentService.afterProperty(properties.dbVersion.getName(), properties);
        assertTrue(properties.authenticationMechanism.getPossibleValues()
                .containsAll(Arrays.asList(MongoDBConnectionProperties.AuthenticationMechanism.NEGOTIATE_MEC,
                        MongoDBConnectionProperties.AuthenticationMechanism.KERBEROS_MEC,
                        MongoDBConnectionProperties.AuthenticationMechanism.PLAIN_MEC)));
        assertEquals(3, properties.authenticationMechanism.getPossibleValues().size());
        properties.dbVersion.setValue(MongoDBConnectionProperties.DBVersion.MONGODB_3_0_X);
        componentService.afterProperty(properties.dbVersion.getName(), properties);
        assertEquals(4, properties.authenticationMechanism.getPossibleValues().size());
        assertEquals(MongoDBConnectionProperties.AuthenticationMechanism.NEGOTIATE_MEC,
                properties.authenticationMechanism.getValue());

        // UseReplicaSet
        properties.useReplicaSet.setValue(true);
        componentService.afterProperty(properties.useReplicaSet.getName(), properties);
        assertTrue(mainForm.getWidget(properties.replicaSetTable).isVisible());
        assertTrue(mainForm.getWidget(properties.host).isHidden());
        assertTrue(mainForm.getWidget(properties.port).isHidden());

    }

    @Test
    public void testI18nForEnumProperty() {
        MongoDBConnectionProperties properties = new MongoDBConnectionProperties("root");
        properties.init();
        assertEquals("MongoDB 2.5.X (Deprecated)",
                properties.dbVersion.getPossibleValuesDisplayName(MongoDBConnectionProperties.DBVersion.MONGODB_2_5_X));
        assertEquals("MongoDB 2.6.X",
                properties.dbVersion.getPossibleValuesDisplayName(MongoDBConnectionProperties.DBVersion.MONGODB_2_6_X));
        assertEquals("MongoDB 3.0.X",
                properties.dbVersion.getPossibleValuesDisplayName(MongoDBConnectionProperties.DBVersion.MONGODB_3_0_X));
        assertEquals("MongoDB 3.2.X",
                properties.dbVersion.getPossibleValuesDisplayName(MongoDBConnectionProperties.DBVersion.MONGODB_3_2_X));

        assertEquals("NEGOTIATE (Recommended for non Kerberized environments)", properties.authenticationMechanism
                .getPossibleValuesDisplayName(MongoDBConnectionProperties.AuthenticationMechanism.NEGOTIATE_MEC));
        assertEquals("PLAIN SASL", properties.authenticationMechanism
                .getPossibleValuesDisplayName(MongoDBConnectionProperties.AuthenticationMechanism.PLAIN_MEC));
        assertEquals("SCRAM-SHA-1 SASL", properties.authenticationMechanism
                .getPossibleValuesDisplayName(MongoDBConnectionProperties.AuthenticationMechanism.SCRAMSHA1_MEC));
        assertEquals("GSSAPI SASL (KERBEROS)", properties.authenticationMechanism
                .getPossibleValuesDisplayName(MongoDBConnectionProperties.AuthenticationMechanism.KERBEROS_MEC));

    }

    @Ignore("Move to other part ?")
    @Test
    public void testValidateConnectionError() throws Throwable {
        MongoDBConnectionProperties properties = new MongoDBConnectionProperties("root");
        properties.init();
        //
        properties.host.setValue("localhost");
        properties.port.setValue(27018);

        ComponentService componentService = getComponentService();
        componentService.validateProperty(properties.testConnection.getName(), properties);

        ValidationResult result = properties.getValidationResult();
        assertEquals(ValidationResult.Result.ERROR, result.getStatus());
        LOGGER.debug(result.getMessage());
    }

    @Ignore("Move to other part ?")
    @Test
    public void testValidateConnection() throws Throwable {
        MongoDBConnectionProperties properties = new MongoDBConnectionProperties("root");
        properties.init();

        properties.host.setValue("localhost");
        properties.port.setValue(27017);

        ComponentService componentService = getComponentService();
        componentService.validateProperty(properties.testConnection.getName(), properties);

        ValidationResult result = properties.getValidationResult();
        assertEquals(ValidationResult.Result.OK, result.getStatus());
        LOGGER.debug(result.getMessage());

        Form wizardForm = properties.getForm(FORM_WIZARD);
        assertTrue(wizardForm.isAllowForward());
        assertFalse(wizardForm.isAllowBack());
        assertFalse(wizardForm.isAllowFinish());
    }

    @Ignore("Need to fix it after wizard part all finish implement")
    @Test
    public void testWizard() {

    }

    protected void checkBasicSetting(Form form, MongoDBConnectionProperties properties) {
        if (Form.MAIN.equals(form.getName()) || FORM_WIZARD.equals(form.getName())) {
            assertTrue(form.getWidget(properties.dbVersion).isVisible());
            assertTrue(form.getWidget(properties.useReplicaSet).isVisible());
            assertTrue(form.getWidget(properties.replicaSetTable).isHidden());
            assertTrue(form.getWidget(properties.host).isVisible());
            assertTrue(form.getWidget(properties.port).isVisible());
            assertTrue(form.getWidget(properties.database).isVisible());
            assertTrue(form.getWidget(properties.requiredAuthentication).isVisible());
            assertTrue(form.getWidget(properties.authenticationMechanism).isHidden());
            assertTrue(form.getWidget(properties.setAuthenticationDatabase).isHidden());
            assertTrue(form.getWidget(properties.authenticationDatabase).isHidden());
            assertTrue(form.getWidget(properties.userPassword).isHidden());
            assertTrue(form.getWidget(properties.kerberos).isHidden());
        }

    }

}
