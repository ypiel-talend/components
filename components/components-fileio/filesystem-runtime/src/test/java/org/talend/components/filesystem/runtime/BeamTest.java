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

package org.talend.components.filesystem.runtime;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.junit.Rule;
import org.junit.Test;
import org.talend.components.filesystem.runtime.test.SparkIntegrationTestResource;

public class BeamTest {

    @Rule
    public SparkIntegrationTestResource spark = SparkIntegrationTestResource.ofLocal();

    public BeamTest() throws IOException, URISyntaxException {
    }

    @Test
    public void test() throws IOException {
        Pipeline pipeline = spark.createPipeline();
        // org.apache.hadoop.security.UserGroupInformation.loginUserFromKeytab("bchen",
        // "/tmp/cdh580ker/hadoop/bchen.keytab");
        pipeline.apply(TextIO.read().from("/user/bchen/in.txt"))
                .apply(TextIO.write().to("/user/bchen/out/"));
        pipeline.run().waitUntilFinish();

    }

}
