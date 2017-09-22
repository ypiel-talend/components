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

import static org.talend.components.mongodb.common.MongoDBConstants.DYNAMIC_PROPERTY_VALUE;
import static org.talend.daikon.properties.property.PropertyFactory.newProperty;

import java.util.List;

import org.apache.commons.lang3.reflect.TypeLiteral;
import org.talend.components.api.properties.ComponentPropertiesImpl;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.property.Property;

/**
 * Function parameters table properties
 */
public class FunctionParametersTable extends ComponentPropertiesImpl {

    private static final TypeLiteral<List<Object>> LIST_STRING_TYPE = new TypeLiteral<List<Object>>() {// empty
    };

    public Property<List<Object>> parameter = newProperty(LIST_STRING_TYPE, "parameter");

    public FunctionParametersTable(String name) {
        super(name);
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form mainForm = new Form(this, Form.MAIN);
        mainForm.addColumn(parameter);
    }

    @Override
    public void setupProperties() {
        super.setupProperties();
        parameter.setTaggedValue(DYNAMIC_PROPERTY_VALUE, true);
    }

}
