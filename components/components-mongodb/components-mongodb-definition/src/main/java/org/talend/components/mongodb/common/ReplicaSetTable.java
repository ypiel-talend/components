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

import static org.talend.daikon.properties.property.PropertyFactory.newProperty;

import java.util.List;

import org.apache.commons.lang3.reflect.TypeLiteral;
import org.talend.components.api.properties.ComponentPropertiesImpl;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.property.Property;

/**
 * ReplicaSet table properties to save host and port list
 */
public class ReplicaSetTable extends ComponentPropertiesImpl {

    private static final TypeLiteral<List<String>> REPLICA_HOST = new TypeLiteral<List<String>>() {// empty
    };

    private static final TypeLiteral<List<Integer>> REPLICA_PORT = new TypeLiteral<List<Integer>>() {// empty
    };

    public Property<List<String>> host = newProperty(REPLICA_HOST, "host");

    public Property<List<Integer>> port = newProperty(REPLICA_PORT, "port");

    public ReplicaSetTable(String name) {
        super(name);
        host.setRequired();
        port.setRequired();
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form mainForm = new Form(this, Form.MAIN);
        mainForm.addColumn(host);
        mainForm.addColumn(port);
    }
}
