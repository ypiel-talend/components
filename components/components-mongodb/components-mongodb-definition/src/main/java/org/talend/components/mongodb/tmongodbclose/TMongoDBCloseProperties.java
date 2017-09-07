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
package org.talend.components.mongodb.tmongodbclose;

import static org.talend.daikon.properties.presentation.Widget.widget;

import org.talend.components.api.properties.ComponentPropertiesImpl;
import org.talend.components.api.properties.ComponentReferenceProperties;
import org.talend.components.mongodb.MongoDBConnectionProperties;
import org.talend.components.mongodb.tmongodbconnection.TMongoDBConnectionDefinition;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;

public class TMongoDBCloseProperties extends ComponentPropertiesImpl {

    public ComponentReferenceProperties<MongoDBConnectionProperties> referencedComponent = new ComponentReferenceProperties<MongoDBConnectionProperties>(
            "referencedComponent", TMongoDBConnectionDefinition.COMPONENT_NAME);

    public TMongoDBCloseProperties(String name) {
        super(name);
    }

    @Override
    public void setupLayout() {
        super.setupLayout();

        Form mainForm = Form.create(this, Form.MAIN);
        Widget compListWidget = widget(referencedComponent).setWidgetType(Widget.COMPONENT_REFERENCE_WIDGET_TYPE);
        mainForm.addRow(compListWidget);
    }
}
