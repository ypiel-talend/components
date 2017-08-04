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

import org.talend.components.api.AbstractComponentFamilyDefinition;
import org.talend.components.api.ComponentInstaller;
import org.talend.components.api.Constants;
import org.talend.daikon.definition.Definition;

import com.google.auto.service.AutoService;

import aQute.bnd.annotation.component.Component;

/**
 * Install all of the definitions provided for the Filesystem family of components.
 */
@AutoService(ComponentInstaller.class)
@Component(name = Constants.COMPONENT_INSTALLER_PREFIX
        + FilesystemComponentFamilyDefinition.NAME, provide = ComponentInstaller.class)
public class FilesystemComponentFamilyDefinition extends AbstractComponentFamilyDefinition implements ComponentInstaller {

    public static final String NAME = "Filesystem";

    public static final String MAVEN_GROUP_ID = "org.talend.components";

    public static final String MAVEN_DEFAULT_RUNTIME_ARTIFACT_ID = "filesystem-runtime";

    public static final String MAVEN_DEFAULT_RUNTIME_URI = "mvn:" + MAVEN_GROUP_ID + "/" + MAVEN_DEFAULT_RUNTIME_ARTIFACT_ID;

    //TODO registry definitions
    public FilesystemComponentFamilyDefinition(Definition<?>[] definitions) {
        super(NAME, definitions);
    }

    @Override
    public void install(ComponentFrameworkContext ctx) {
        ctx.registerComponentFamilyDefinition(this);
    }
}
