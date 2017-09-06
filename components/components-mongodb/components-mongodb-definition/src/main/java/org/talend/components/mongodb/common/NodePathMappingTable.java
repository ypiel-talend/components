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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.reflect.TypeLiteral;
import org.talend.components.common.BasedOnSchemaTable;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.property.Property;

public class NodePathMappingTable extends BasedOnSchemaTable {

    private static final TypeLiteral<List<String>> LIST_STRING_TYPE = new TypeLiteral<List<String>>() {// empty
    };

    private static final TypeLiteral<List<Boolean>> LIST_BOOLEAN_TYPE = new TypeLiteral<List<Boolean>>() {// empty
    };

    public Property<List<String>> nodePath = newProperty(LIST_STRING_TYPE, "nodePath");

    public Property<List<Boolean>> removeNullField = newProperty(LIST_BOOLEAN_TYPE, "removeNullField");

    private boolean hasRemoveNull;

    public NodePathMappingTable(String name) {
        super(name);
    }

    public NodePathMappingTable(String name, boolean hasRemoveNull) {
        super(name);
        this.hasRemoveNull = hasRemoveNull;
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form mainForm = getForm(Form.MAIN);
        mainForm.addColumn(nodePath);
        if (hasRemoveNull) {
            mainForm.addColumn(removeNullField);
        }
    }

    public void updateTable(List<String> fieldNames) {
        if (fieldNames != null && fieldNames.size() > 0) {
            Object oldColumnList = columnName.getValue();
            Object oldNodePaths = nodePath.getValue();
            Object oldRemoveNullField = null;

            if (hasRemoveNull) {
                oldRemoveNullField = removeNullField.getValue();
            }

            List<String> newNodePaths = new ArrayList<>();
            List<Boolean> newRemoveNullFields = new ArrayList<>();
            for (int i = 0; i < fieldNames.size(); i++) {
                String fieldName = fieldNames.get(i);
                if (oldColumnList != null && oldColumnList instanceof List) {
                    int index = ((List<String>) oldColumnList).indexOf(fieldName);
                    if (index > -1) {
                        if (oldNodePaths != null && oldNodePaths instanceof List) {
                            if (index > ((List<String>) oldNodePaths).size()) {
                                newNodePaths.add(null);
                            } else {
                                newNodePaths.add(((List<String>) oldNodePaths).get(index));
                            }
                        }
                        if (hasRemoveNull && oldRemoveNullField != null && oldRemoveNullField instanceof List) {
                            if (index > ((List<Boolean>) oldRemoveNullField).size()) {
                                newRemoveNullFields.add(false);
                            } else {
                                newRemoveNullFields.add(((List<Boolean>) oldRemoveNullField).get(index));
                            }
                        }
                    }
                } else {
                    newNodePaths.add(null);
                    if (hasRemoveNull) {
                        newRemoveNullFields.add(false);
                    }
                }
            }
            columnName.setValue(fieldNames);
            nodePath.setValue(newNodePaths);
            if (hasRemoveNull) {
                removeNullField.setValue(newRemoveNullFields);
            }
        }
    }

}
