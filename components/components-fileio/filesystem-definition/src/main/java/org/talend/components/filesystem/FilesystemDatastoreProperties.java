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

package org.talend.components.filesystem;

import static org.talend.daikon.properties.presentation.Widget.widget;

import java.util.EnumSet;

import org.talend.components.common.datastore.DatastoreProperties;
import org.talend.daikon.properties.PropertiesImpl;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;

public class FilesystemDatastoreProperties extends PropertiesImpl implements DatastoreProperties {

    public Property<FilesystemType> filesystemType = PropertyFactory.newEnum("filesystemType", FilesystemType.class)
            .setValue(FilesystemType.HDFS);

    // HDFS properties
    public Property<Boolean> useKerberos = PropertyFactory.newBoolean("useKerberos", false);

    public Property<String> kerberosPrincipal = PropertyFactory.newString("kerberosPrincipal", "username@EXAMPLE.COM");

    public Property<String> kerberosKeytab = PropertyFactory.newString("kerberosKeytab", "/home/username/username.keytab");

    public Property<String> userName = PropertyFactory.newString("userName");

    // S3 properties
    public Property<Boolean> specifyCredentials = PropertyFactory.newBoolean("specifyCredentials", true).setRequired();

    public Property<String> accessKey = PropertyFactory.newString("accessKey");

    public Property<String> secretKey = PropertyFactory.newString("secretKey")
            .setFlags(EnumSet.of(Property.Flags.ENCRYPT, Property.Flags.SUPPRESS_LOGGING));

    public FilesystemDatastoreProperties(String name) {
        super(name);
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form mainForm = new Form(this, Form.MAIN);
        mainForm.addRow(useKerberos);
        mainForm.addRow(kerberosPrincipal);
        mainForm.addRow(kerberosKeytab);
        mainForm.addRow(userName);

        mainForm.addRow(specifyCredentials);
        mainForm.addRow(accessKey);
        mainForm.addRow(widget(secretKey).setWidgetType(Widget.HIDDEN_TEXT_WIDGET_TYPE));
    }

    @Override
    public void refreshLayout(Form form) {
        super.refreshLayout(form);
        // Main properties
        if (form.getName().equals(Form.MAIN)) {
            boolean isHDFS = filesystemType.getValue() == FilesystemType.HDFS;
            boolean isS3 = filesystemType.getValue() == FilesystemType.S3;
            boolean isGCS = filesystemType.getValue() == FilesystemType.GCS;

            // handle HDFS
            if (isHDFS) {
                form.getWidget(useKerberos.getName()).setVisible();
                form.getWidget(kerberosPrincipal.getName()).setVisible(useKerberos);
                form.getWidget(kerberosKeytab.getName()).setVisible(useKerberos);
                form.getWidget(userName.getName()).setHidden(useKerberos);
            } else {
                form.getWidget(useKerberos.getName()).setHidden();
                form.getWidget(kerberosPrincipal.getName()).setHidden();
                form.getWidget(kerberosKeytab.getName()).setHidden();
                form.getWidget(userName.getName()).setHidden();
            }

            // handle S3
            if (isS3) {
                form.getWidget(specifyCredentials.getName()).setVisible();
                final boolean isSpecifyCredentialsEnabled = specifyCredentials.getValue();
                accessKey.setRequired(isSpecifyCredentialsEnabled);
                secretKey.setRequired(isSpecifyCredentialsEnabled);
                form.getWidget(accessKey.getName()).setVisible(isSpecifyCredentialsEnabled);
                form.getWidget(secretKey.getName()).setVisible(isSpecifyCredentialsEnabled);
            } else {
                form.getWidget(specifyCredentials.getName()).setHidden();
                accessKey.setRequired(false);
                secretKey.setRequired(false);
                form.getWidget(accessKey.getName()).setHidden();
                form.getWidget(secretKey.getName()).setHidden();
            }

        }
    }

    public void afterUseKerberos() {
        refreshLayout(getForm(Form.MAIN));
    }

    public void afterSpecifyCredentials() {
        refreshLayout(getForm(Form.MAIN));
    }

    public enum FilesystemType {
        HDFS,
        S3, // Amazon S3
        GCS, // Google cloud Storage
    }
}
