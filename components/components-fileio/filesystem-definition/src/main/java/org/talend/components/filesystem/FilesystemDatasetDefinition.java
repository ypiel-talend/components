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

import org.talend.components.common.dataset.DatasetDefinition;
import org.talend.daikon.definition.DefinitionImageType;
import org.talend.daikon.definition.I18nDefinition;
import org.talend.daikon.runtime.RuntimeInfo;

public class FilesystemDatasetDefinition extends I18nDefinition implements DatasetDefinition<FilesystemDatasetProperties> {

    public FilesystemDatasetDefinition(String name) {
        super(name);
    }

    @Override
    public RuntimeInfo getRuntimeInfo(FilesystemDatasetProperties properties) {
        return null;
    }

    @Override
    public Class<FilesystemDatasetProperties> getPropertiesClass() {
        return null;
    }

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
        return null;
    }
}
