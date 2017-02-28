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

package org.talend.components.bigquery;

import org.junit.Ignore;
import org.junit.Test;

import org.talend.daikon.runtime.RuntimeInfo;

import static org.junit.Assert.assertEquals;

public class BigQueryDatastoreDefinitionTest {

    private final BigQueryDatastoreDefinition datastoreDefinition = new BigQueryDatastoreDefinition();

    /**
    * Check {@link BigQueryDatastoreDefinition#getRuntimeInfo(BigQueryDatastoreProperties) returns RuntimeInfo,
    * which runtime class name is "org.talend.components.bigquery.runtime.BigQueryDatastoreRuntime"
    */
    @Test
    @Ignore("This can't work unless the runtime jar is already installed in maven!")
    public void testRuntimeInfo() {
        RuntimeInfo runtimeInfo = datastoreDefinition.getRuntimeInfo(null);
        assertEquals("org.talend.components.bigquery.runtime.BigQueryDatastoreRuntime", runtimeInfo.getRuntimeClassName());
    }
}
