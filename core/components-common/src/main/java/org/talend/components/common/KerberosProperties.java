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
package org.talend.components.common;

import static org.talend.daikon.properties.property.PropertyFactory.newProperty;

import org.talend.daikon.properties.PropertiesImpl;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.property.Property;

public class KerberosProperties extends PropertiesImpl {

    public Property<String> userPrincipal = newProperty("userPrincipal");

    public Property<String> realm = newProperty("realm");

    public Property<String> kdcServer = newProperty("kdcServer");

    public KerberosProperties(String name) {
        super(name);
    }

    @Override
    public void setupProperties() {
        super.setupProperties();
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form form = Form.create(this, Form.MAIN);
        form.addRow(userPrincipal);
        form.addRow(realm);
        form.addRow(kdcServer);
    }

    @Override
    public void refreshLayout(Form form) {
        super.refreshLayout(form);
    }

}
