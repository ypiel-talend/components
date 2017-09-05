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

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.reflect.TypeLiteral;
import org.talend.components.common.BasedOnSchemaTable;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.property.Property;

public class SortByTable extends BasedOnSchemaTable {

    private static final TypeLiteral<List<String>> LIST_STRING_TYPE = new TypeLiteral<List<String>>() {// empty
    };

    public Property<List<String>> order = newProperty(LIST_STRING_TYPE, "order");

    public SortByTable(String name) {
        super(name);
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form mainForm = getForm(Form.MAIN);
        mainForm.addColumn(order);
    }

    @Override
    public void setupProperties() {
        super.setupProperties();
        order.setPossibleValues(Arrays.asList("asc", "desc"));
    }

}
