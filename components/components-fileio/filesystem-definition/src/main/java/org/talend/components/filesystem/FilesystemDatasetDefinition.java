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
import org.talend.components.common.dataset.DatasetDefinition;
import org.talend.daikon.definition.DefinitionImageType;
import org.talend.daikon.definition.I18nDefinition;
import org.talend.daikon.runtime.RuntimeInfo;

public class FilesystemDatasetDefinition extends I18nDefinition implements DatasetDefinition<FilesystemDatasetProperties> {

    public static final String RUNTIME = "org.talend.components.filesystem.runtime.FilesystemDatasetRuntime";

    public static final String NAME = FilesystemComponentFamilyDefinition.NAME + "Dataset";

    /**
     * Setting this to false deactivates ClassLoader reuse, which can leak state between requests. Setting this to false
     * will correct TCOMP-422 (service fails in a Kerberos/KMS environment after a certain time), but causes TCOMP-486
     * (serious memory leaks in the sandboxed environment). TODO: Ensure that both TCOMP-422 and TCOMP-486 can be fixed.
     */
    public static final boolean IS_CLASSLOADER_REUSABLE = true;

    public FilesystemDatasetDefinition() {
        super(NAME);
    }

    @Override
    public RuntimeInfo getRuntimeInfo(FilesystemDatasetProperties properties) {
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
    public Class<FilesystemDatasetProperties> getPropertiesClass() {
        return FilesystemDatasetProperties.class;
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
