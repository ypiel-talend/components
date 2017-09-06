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

package org.talend.components.mongodb.tmongodbinput;

import static org.talend.daikon.properties.presentation.Widget.widget;
import static org.talend.daikon.properties.property.PropertyFactory.newBoolean;
import static org.talend.daikon.properties.property.PropertyFactory.newEnum;
import static org.talend.daikon.properties.property.PropertyFactory.newProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.avro.Schema;
import org.talend.components.api.component.ISchemaListener;
import org.talend.components.api.component.PropertyPathConnector;
import org.talend.components.mongodb.MongoDBConnectionProperties;
import org.talend.components.mongodb.common.AggregationStagesTable;
import org.talend.components.mongodb.common.MongoDBBaseProperties;
import org.talend.components.mongodb.common.NodePathMappingTable;
import org.talend.components.mongodb.common.SortByTable;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;

public class TMongoDBInputProperties extends MongoDBBaseProperties {

    public enum ReadPreference {
        PRIMARY,
        PRIMARY_PREFERRED,
        SECONDARY,
        SECONDARY_PREFERRED,
        NEAREST
    }

    public Property<Boolean> setReadPreference = newBoolean("setReadPreference");

    public Property<ReadPreference> readPreference = newEnum("readPreference", ReadPreference.class);

    public enum QueryType {
        FIND_QUERY,
        AGGREGATION_QUERY
    }

    public Property<QueryType> queryType = newEnum("queryType", QueryType.class);

    public Property<String> query = newProperty("query"); //$NON-NLS-1$

    public AggregationStagesTable aggStages = new AggregationStagesTable("aggStages");

    public NodePathMappingTable mapping = new NodePathMappingTable("mapping");

    public SortByTable sort = new SortByTable("sort");

    public Property<String> limit = newProperty("limit"); //$NON-NLS-1$

    public Property<Boolean> extSortAgg = newBoolean("extSortAgg");

    public TMongoDBInputProperties(String name) {
        super(name);
    }

    @Override
    public void setupProperties() {
        super.setupProperties();
        readPreference.setValue(ReadPreference.PRIMARY);
        query.setValue("{}");
        collection.setSchemaListener(new ISchemaListener() {

            @Override
            public void afterSchema() {
                beforeMapping();
            }
        });
    }

    public void beforeMapping() {
        mapping.updateTable(getFieldNames(collection.main.schema.getValue()));
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form mainForm = getForm(Form.MAIN);
        mainForm.addRow(setReadPreference);
        mainForm.addRow(widget(readPreference).setWidgetType(Widget.ENUMERATION_WIDGET_TYPE));
        mainForm.addRow(widget(queryType).setWidgetType(Widget.ENUMERATION_WIDGET_TYPE));
        mainForm.addRow(widget(query).setWidgetType(Widget.TEXT_AREA_WIDGET_TYPE));
        mainForm.addRow(widget(aggStages).setWidgetType(Widget.TABLE_WIDGET_TYPE));
        mainForm.addRow(widget(mapping).setWidgetType(Widget.TABLE_WIDGET_TYPE));
        mainForm.addRow(widget(sort).setWidgetType(Widget.TABLE_WIDGET_TYPE));
        mainForm.addRow(limit);

        Form advancedForm = getForm(Form.ADVANCED);
        advancedForm.addRow(extSortAgg);
    }

    @Override
    public void refreshLayout(Form form) {
        super.refreshLayout(form);
        boolean findQuery = QueryType.FIND_QUERY.equals(queryType.getValue());
        if (form.getName().equals(Form.MAIN)) {
            form.getWidget(query).setVisible(findQuery);
            form.getWidget(aggStages).setHidden(findQuery);
            form.getWidget(sort).setVisible(findQuery);
            form.getWidget(limit).setVisible(findQuery);
            if (MongoDBConnectionProperties.DBVersion.MONGODB_2_5_X.equals(connection.dbVersion.getValue())) {
                queryType.setPossibleValues(QueryType.FIND_QUERY);
            } else {
                queryType.setPossibleValues(QueryType.values());
            }
        } else if (form.getName().equals(Form.ADVANCED)) {
            form.getWidget(extSortAgg).setHidden(findQuery);
        }
    }

    @Override
    protected Set<PropertyPathConnector> getAllSchemaPropertiesConnectors(boolean isOutputConnection) {
        if (isOutputConnection) {
            return Collections.singleton(MAIN_CONNECTOR);
        } else {
            return Collections.emptySet();
        }
    }

    public void afterSetReadPreference() {
        refreshLayout(getForm(Form.MAIN));
    }

    public void afterQueryType() {
        refreshLayout(getForm(Form.MAIN));
        refreshLayout(getForm(Form.ADVANCED));
    }

}
