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

import org.talend.components.common.BasedOnSchemaTable;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;

public class UpsertFieldTable extends BasedOnSchemaTable {

    public UpsertFieldTable(String name) {
        super(name);
    }

    @Override
    public void setupLayout() {
        Form mainForm = new Form(this, Form.MAIN);
        mainForm.addColumn(Widget.widget(columnName).setWidgetType(Widget.ENUMERATION_WIDGET_TYPE));
    }
}
