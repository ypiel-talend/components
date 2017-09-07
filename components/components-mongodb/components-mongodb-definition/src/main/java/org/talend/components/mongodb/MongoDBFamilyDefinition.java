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
package org.talend.components.mongodb;

import org.talend.components.api.AbstractComponentFamilyDefinition;
import org.talend.components.api.ComponentInstaller;
import org.talend.components.api.Constants;
import org.talend.components.mongodb.tmongodbbulkload.TMongoDBBulkLoadDefinition;
import org.talend.components.mongodb.tmongodbclose.TMongoDBCloseDefinition;
import org.talend.components.mongodb.tmongodbconnection.TMongoDBConnectionDefinition;
import org.talend.components.mongodb.tmongodbinput.TMongoDBInputDefinition;
import org.talend.components.mongodb.tmongodboutput.TMongoDBOutputDefinition;
import org.talend.components.mongodb.tmongodbrow.TMongoDBRowDefinition;
import org.talend.components.mongodb.wizard.MongoDBWizardDefinition;

import com.google.auto.service.AutoService;

import aQute.bnd.annotation.component.Component;

/**
 * Install all of the definitions provided for the MongoDB family of components.
 */
@AutoService(ComponentInstaller.class)
@Component(name = Constants.COMPONENT_INSTALLER_PREFIX + MongoDBFamilyDefinition.NAME, provide = ComponentInstaller.class)
public class MongoDBFamilyDefinition extends AbstractComponentFamilyDefinition implements ComponentInstaller {

    public static final String NAME = "MongoDB";

    public MongoDBFamilyDefinition() {
        super(NAME, //
                new TMongoDBConnectionDefinition(), //
                new TMongoDBInputDefinition(), //
                new TMongoDBOutputDefinition(), //
                new TMongoDBRowDefinition(), //
                new TMongoDBBulkLoadDefinition(), //
                new TMongoDBCloseDefinition(), //
                new MongoDBWizardDefinition());//

    }

    @Override
    public void install(ComponentFrameworkContext ctx) {
        ctx.registerComponentFamilyDefinition(this);
    }
}
