//  ============================================================================
//
//  Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
//  This source code is available under agreement available at
//  %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
//  You should have received a copy of the agreement
//  along with this program; if not, write to Talend SA
//  9 rue Pages 92150 Suresnes, France
//
//  ============================================================================
package org.talend.components.mongodb.wizard;

import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.api.wizard.ComponentWizard;
import org.talend.components.api.wizard.ComponentWizardDefinition;
import org.talend.components.mongodb.MongoDBConnectionProperties;

public class MongoDBWizard extends ComponentWizard {

    MongoDBConnectionProperties cProps;

    MongoDBWizard(ComponentWizardDefinition def, String repositoryLocation) {
        super(def, repositoryLocation);

        cProps = new MongoDBConnectionProperties("mongodb").setRepositoryLocation(getRepositoryLocation());
        cProps.init();
        addForm(cProps.getForm(MongoDBConnectionProperties.FORM_WIZARD));

    }

    public boolean supportsProperties(ComponentProperties properties) {
        return properties instanceof MongoDBConnectionProperties;
    }

    public void setupProperties(MongoDBConnectionProperties cPropsOther) {
        cProps.copyValuesFrom(cPropsOther);
    }

}
