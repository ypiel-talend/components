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

import org.talend.components.api.component.Connector;
import org.talend.components.api.component.PropertyPathConnector;
import org.talend.components.common.FixedConnectorsComponentProperties;
import org.talend.components.common.SchemaProperties;
import org.talend.daikon.properties.presentation.Form;

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
    
    /**
     * This field specifies path {@link SchemaProperties} associated with some
     * connector. This is used to retrieve schema value from
     * {@link FixedConnectorsComponentProperties} class
     */
    protected final transient PropertyPathConnector mainConnector = new PropertyPathConnector(Connector.MAIN_NAME, "schema"); //$NON-NLS-1$

    /**
     * Design schema of input component. Design schema defines data fields which
     * should be retrieved from Data Store. In this component example Data Store
     * is a single file on file system
     */
    public final SchemaProperties schema = new SchemaProperties("schema"); //$NON-NLS-1$


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

        }
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
