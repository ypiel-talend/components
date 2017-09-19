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

import java.net.MalformedURLException;
import java.net.URL;

import org.talend.components.api.component.runtime.DependenciesReader;
import org.talend.components.api.component.runtime.JarRuntimeInfo;
import org.talend.components.api.exception.ComponentException;
import org.talend.components.common.dataset.DatasetProperties;
import org.talend.components.common.datastore.DatastoreDefinition;
import org.talend.components.filesystem.input.FilesystemInputDefinition;
import org.talend.components.filesystem.output.FilesystemOutputDefinition;
import org.talend.daikon.definition.DefinitionImageType;
import org.talend.daikon.definition.I18nDefinition;
import org.talend.daikon.runtime.RuntimeInfo;

public class FilesystemDatastoreDefinition extends I18nDefinition implements DatastoreDefinition<FilesystemDatastoreProperties> {

    public static final String RUNTIME = "org.talend.components.filesystem.runtime.FilesystemDatastoreRuntime";

    public static final String NAME = FilesystemComponentFamilyDefinition.NAME + "Datastore";

    /** TODO: See {@link org.talend.components.filesystem.FilesystemDatasetDefinition}. */
    public static final boolean IS_CLASSLOADER_REUSABLE = FilesystemDatasetDefinition.IS_CLASSLOADER_REUSABLE;

    public FilesystemDatastoreDefinition() {
        super(NAME);
    }

    @Override
    public DatasetProperties createDatasetProperties(FilesystemDatastoreProperties storeProp) {
        FilesystemDatasetProperties dataset = new FilesystemDatasetProperties("dataset");
        dataset.init();
        dataset.setDatastoreProperties(storeProp);
        return dataset;
    }

    @Override
    public String getInputCompDefinitionName() {
        return FilesystemInputDefinition.NAME;
    }

    @Override
    public String getOutputCompDefinitionName() {
        return FilesystemOutputDefinition.NAME;
    }

    @Override
    public RuntimeInfo getRuntimeInfo(FilesystemDatastoreProperties properties) {
        try {
            return new JarRuntimeInfo(new URL(FilesystemComponentFamilyDefinition.MAVEN_DEFAULT_RUNTIME_URI),
                    DependenciesReader.computeDependenciesFilePath(FilesystemComponentFamilyDefinition.MAVEN_GROUP_ID,
                            FilesystemComponentFamilyDefinition.MAVEN_DEFAULT_RUNTIME_ARTIFACT_ID),
                    RUNTIME, IS_CLASSLOADER_REUSABLE);
        } catch (MalformedURLException e) {
            throw new ComponentException(e);
        }
    }

    @Override
    public Class<FilesystemDatastoreProperties> getPropertiesClass() {
        return FilesystemDatastoreProperties.class;
    }

    @Deprecated
    @Override
    public String getImagePath() {
        return null;
    }

    @Override
    public String getImagePath(DefinitionImageType definitionImageType) {
        return null;
    }

    @Override
    public String getIconKey() {
        return "file-hdfs-o";
    }
}
