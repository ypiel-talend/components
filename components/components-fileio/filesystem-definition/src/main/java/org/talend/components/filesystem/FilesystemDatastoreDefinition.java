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
import org.talend.components.common.datastore.DatastoreDefinition;
import org.talend.components.common.datastore.DatastoreProperties;
import org.talend.daikon.definition.DefinitionImageType;
import org.talend.daikon.definition.I18nDefinition;
import org.talend.daikon.runtime.RuntimeInfo;

public class FilesystemDatastoreDefinition extends I18nDefinition implements DatastoreDefinition{

    public static final String RUNTIME = "org.talend.components.filesystem.runtime.FilesystemDatastoreRuntime";

    public static final String NAME = FilesystemComponentFamilyDefinition.NAME + "Datastore";

//    /** TODO: See {@link org.talend.components.simplefileio.SimpleFileIODatasetDefinition}. */
//    public static final boolean IS_CLASSLOADER_REUSABLE = SimpleFileIODatasetDefinition.IS_CLASSLOADER_REUSABLE;

    public FilesystemDatastoreDefinition() {
        super(NAME);
    }

    @Override
    public DatasetProperties createDatasetProperties(DatastoreProperties storeProp) {
        return null;
    }

    @Override
    public String getInputCompDefinitionName() {
        return null;
    }

    @Override
    public String getOutputCompDefinitionName() {
        return null;
    }

    @Override
    public RuntimeInfo getRuntimeInfo(DatastoreProperties properties) {
        return null;
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
