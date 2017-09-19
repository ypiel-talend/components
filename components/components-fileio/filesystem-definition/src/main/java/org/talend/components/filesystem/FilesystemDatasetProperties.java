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

import org.talend.components.common.dataset.DatasetProperties;
import org.talend.components.simplefileio.SimpleFileIODatasetProperties;
import org.talend.components.simplefileio.s3.S3DatasetProperties;
import org.talend.daikon.properties.PropertiesImpl;
import org.talend.daikon.properties.ReferenceProperties;
import org.talend.daikon.properties.presentation.Form;

public class FilesystemDatasetProperties extends PropertiesImpl implements DatasetProperties<FilesystemDatastoreProperties> {

    public final transient ReferenceProperties<FilesystemDatastoreProperties> datastoreRef = new ReferenceProperties<>(
            "datastoreRef", FilesystemDatastoreDefinition.NAME);

    public SimpleFileIODatasetProperties simpleDataset = new SimpleFileIODatasetProperties("simpleDataset");

    public S3DatasetProperties s3Dataset = new S3DatasetProperties("s3Dataset");

    public FilesystemDatasetProperties(String name) {
        super(name);
    }

    @Override
    public FilesystemDatastoreProperties getDatastoreProperties() {
        return datastoreRef.getReference();
    }

    @Override
    public void setDatastoreProperties(FilesystemDatastoreProperties datastoreProperties) {
        datastoreRef.setReference(datastoreProperties);
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form mainForm = new Form(this, Form.MAIN);
        mainForm.addRow(simpleDataset.getForm(Form.MAIN));
        mainForm.addRow(s3Dataset.getForm(Form.MAIN));
    }

    @Override
    public void refreshLayout(Form form) {
        super.refreshLayout(form);
        // Main properties
        if (form.getName().equals(Form.MAIN)) {
            FilesystemDatastoreProperties datastore = datastoreRef.getReference();
            boolean isHDFS = datastore.filesystemType.getValue() == FilesystemDatastoreProperties.FilesystemType.HDFS;
            boolean isS3 = datastore.filesystemType.getValue() == FilesystemDatastoreProperties.FilesystemType.S3;
            boolean isGCS = datastore.filesystemType.getValue() == FilesystemDatastoreProperties.FilesystemType.GCS;

            form.getWidget(simpleDataset.getName()).setVisible(isHDFS || isGCS);
            form.getWidget(s3Dataset.getName()).setVisible(isS3);
        }
    }

}
