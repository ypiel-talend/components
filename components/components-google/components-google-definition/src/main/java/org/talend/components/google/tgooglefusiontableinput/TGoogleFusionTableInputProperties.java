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
package org.talend.components.google.tgooglefusiontableinput;

import java.util.Collections;
import java.util.Set;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.talend.components.api.component.Connector;
import org.talend.components.api.component.PropertyPathConnector;
import org.talend.components.common.FixedConnectorsComponentProperties;
import org.talend.components.common.SchemaProperties;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;

/**
 * The ComponentProperties subclass provided by a component stores the
 * configuration of a component and is used for:
 * 
 * <ol>
 * <li>Specifying the format and type of information (properties) that is
 * provided at design-time to configure a component for run-time,</li>
 * <li>Validating the properties of the component at design-time,</li>
 * <li>Containing all of the UI information for laying out and presenting the
 * properties to the user.</li>
 * </ol>
 * 
 * The TGoogleFusionTableInputProperties has following properties:
 * <ol>
 * <li>{@code filename}, a simple property which is a String containing the
 * file path that this component will read.</li>
 * <li>{@code schema}, an embedded property referring to a Schema.</li>
 * <li>{@code delimiter}, a string property containing field delimiter,
 * which is used in a file that this component will read.</li>
 * </ol>
 */
public class TGoogleFusionTableInputProperties extends FixedConnectorsComponentProperties {

    private static final long serialVersionUID = 7685156784461044471L;

    public static enum QueryMode {
        Table,
        Column,
        Row
    }

    public static final Property<QueryMode> resourceType = PropertyFactory.newEnum("resourceType", QueryMode.class).setRequired();

    /**
     * Out of band (a.k.a flow variables) data schema
     * 
     * It has one field: int currentLine
     */
    public static final Schema outOfBandSchema;

    /**
     * Define a text field to write the content of the json credentials file
     * 
     */
    public static final Property<String> clientSecret = PropertyFactory.newString("clientSecret"); //$NON-NLS-1$

    /**
     * Define a text field to write table ID from which to read data
     * 
     */
    public static final Property<String> fusionTable = PropertyFactory.newString("fusionTable"); //$NON-NLS-1$

    /**
     * Define a text field to write column ID from which to read data
     * 
     */
    public static final Property<String> fusionColumn = PropertyFactory.newString("fusionColumn"); //$NON-NLS-1$

    /**
     * Define a text field to write query to select rows
     * 
     */
    public static final Property<String> fusionQuery = PropertyFactory.newString("fusionQuery"); //$NON-NLS-1$

    /**
     * Design schema of input component. Design schema defines data fields which
     * should be retrieved from Data Store. In this component example Data Store
     * is a single file on file system
     */
    public final SchemaProperties schema = new SchemaProperties("schema"); //$NON-NLS-1$

    /**
     * This field specifies path {@link SchemaProperties} associated with some
     * connector. This is used to retrieve schema value from
     * {@link FixedConnectorsComponentProperties} class
     */
    protected final transient PropertyPathConnector mainConnector = new PropertyPathConnector(Connector.MAIN_NAME, "schema"); //$NON-NLS-1$

    /**
     * Sets Out of band schema. This schema is not supposed to be changed by user
     */
    static {
        Field currentLineField = new Field("CURRENT_LINE", Schema.create(Schema.Type.INT), null, (Object) null);
        outOfBandSchema = Schema.createRecord("OutOfBand", null, null, false);
        outOfBandSchema.setFields(Collections.singletonList(currentLineField));
    }

    public TGoogleFusionTableInputProperties(String name) {
        super(name);
    }

    /**
     * Sets UI elements layout on the form {@link Form#addRow()} sets new
     * element under previous one {@link Form#addColumn()} sets new element to
     * the right of previous one in the same row
     * 
     * Note: first line in this method should be
     * <code>super.setupLayout();</code>
     */
    @Override
    public void setupLayout() {
        super.setupLayout();
        Form form = Form.create(this, Form.MAIN);
        form.addRow(schema.getForm(Form.REFERENCE));
        form.addRow(resourceType);
        form.addRow(fusionTable);
        form.addRow(fusionColumn);
        form.addRow(Widget.widget(fusionQuery).setWidgetType(Widget.TEXT_AREA_WIDGET_TYPE));
        form.addRow(Widget.widget(clientSecret).setWidgetType(Widget.TEXT_AREA_WIDGET_TYPE));
        // form.addRow(Widget.widget(filename).setWidgetType(Widget.FILE_WIDGET_TYPE));
        // form.addRow(useCustomDelimiter);
        // form.addColumn(delimiter);
        // form.addColumn(customDelimiter);
        // form.addRow(Widget.widget(guessSchema).setWidgetType(Widget.BUTTON_WIDGET_TYPE));
    }

    /**
     * Refreshes <code>form</code> layout after some changes. Often it is used
     * to show or hide some UI elements
     * 
     * Note: first line in this method should be
     * <code>super.refreshLayout(form);</code>
     */
    @Override
    public void refreshLayout(Form form) {
        super.refreshLayout(form);

        if (form.getName().equals(Form.MAIN)) {
            form.getWidget(fusionTable).setHidden(resourceType.getValue() == QueryMode.Row);
            form.getWidget(fusionColumn).setVisible(resourceType.getValue() == QueryMode.Column);
            form.getWidget(fusionQuery).setVisible(resourceType.getValue() == QueryMode.Row);

        }
    }

    public void afterQueryMode() {
        refreshLayout(getForm(Form.MAIN));
    }

    /**
     * Callback method. Runtime Platform calls it after changes with UI element
     * This method should have name if following format {@code after
     * <PropertyName>}
     */
    public ValidationResult afterClientSecret() {
        return validateJson(clientSecret.getValue());
        // refreshLayout(getForm(Form.MAIN));
    }

    private ValidationResult validateJson(String value) {
        // TODO Auto-generated method stub
        return ValidationResult.OK;
    }

    /**
     * Returns input or output component connectors
     * 
     * @param isOutputConnectors
     * specifies what connectors to return, true if output connectors
     * are requires, false if input connectors are requires
     * @return component connectors
     */
    @Override
    protected Set<PropertyPathConnector> getAllSchemaPropertiesConnectors(boolean isOutputConnectors) {
        if (isOutputConnectors) {
            return Collections.singleton(mainConnector);
        }
        return Collections.emptySet();
    }
}
