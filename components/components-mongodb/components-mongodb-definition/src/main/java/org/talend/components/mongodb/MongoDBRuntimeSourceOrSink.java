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

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.talend.components.api.component.runtime.SourceOrSink;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.daikon.NamedThing;
import org.talend.daikon.properties.property.Property;

public interface MongoDBRuntimeSourceOrSink extends SourceOrSink {

    /**
     * Get the list of database names available for this {@code SourceOrSink} or an empty List if none.
     */
    List<NamedThing> getDatabaseNames(RuntimeContainer container) throws IOException;

    /**
     * Get the list of database names available for this {@code SourceOrSink} or an empty List if none.
     */
    List<NamedThing> getCollectionNames(RuntimeContainer container, List<NamedThing> database) throws IOException;

    /**
     * Get the mapping of database and collection which have selected
     */
    Map<String, List<String>> getDBCollectionMapping(Property<List<NamedThing>> selectedCollectionNames);
}
