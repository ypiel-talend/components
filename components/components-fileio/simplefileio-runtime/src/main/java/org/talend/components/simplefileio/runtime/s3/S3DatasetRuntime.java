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

package org.talend.components.simplefileio.runtime.s3;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.runners.direct.DirectOptions;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.transforms.Sample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.adapter.beam.BeamLocalRunnerOption;
import org.talend.components.adapter.beam.transform.DirectConsumerCollector;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.simplefileio.s3.S3DatasetProperties;
import org.talend.components.simplefileio.s3.input.S3InputProperties;
import org.talend.components.simplefileio.s3.runtime.IS3DatasetRuntime;
import org.talend.daikon.java8.Consumer;
import org.talend.daikon.properties.ValidationResult;

import com.talend.shaded.com.amazonaws.services.s3.AmazonS3;
import com.talend.shaded.com.amazonaws.services.s3.model.Bucket;

public class S3DatasetRuntime implements IS3DatasetRuntime {

    private static final Logger LOG = LoggerFactory.getLogger(S3DatasetRuntime.class);

    /**
     * The dataset instance that this runtime is configured for.
     */
    private S3DatasetProperties properties = null;

    @Override
    public Set<String> listBuckets() {
        AmazonS3 conn = S3Connection.createClient(properties.getDatastoreProperties());
        LOG.debug("Start to find buckets");
        List<Bucket> buckets = conn.listBuckets();
        Set<String> bucketsName = new HashSet<>();
        for (Bucket bucket : buckets) {
            bucketsName.add(bucket.getName());
        }
        return bucketsName;
    }

    @Override
    public Schema getSchema() {
        // Simple schema container.
        final Schema[] s = new Schema[1];
        // Try to get one record and determine its schema in a callback.
        getSample(1, new Consumer<IndexedRecord>() {

            @Override
            public void accept(IndexedRecord in) {
                s[0] = in.getSchema();
            }
        });
        // Return the discovered schema.
        return s[0];
    }

    //getSample is not a good name for the data set interface, as sometimes, it is used to fetch all data in data set definition, not sample
    //Or if it's a good name and only for get sample, for the rest api to get all data in data set should not call this method, should call another interface for
    //fetch all data.
    @Override
    public void getSample(int limit, Consumer<IndexedRecord> consumer) {
        // Create an input runtime based on the properties.
        S3InputRuntime inputRuntime = new S3InputRuntime();
        S3InputProperties inputProperties = new S3InputProperties(null);
        inputProperties.limit.setValue(limit);
        inputProperties.init();
        inputProperties.setDatasetProperties(properties);
        inputRuntime.initialize(null, inputProperties);
        // Create a pipeline using the input component to get records.

        DirectOptions options = BeamLocalRunnerOption.getOptions();
        final Pipeline p = Pipeline.create(options);

        try (DirectConsumerCollector<IndexedRecord> collector = DirectConsumerCollector.of(consumer)) {
            // Collect a sample of the input records.
            p.apply(inputRuntime) //
                    .apply(collector);
            p.run().waitUntilFinish();
        }
    }

    @Override
    public ValidationResult initialize(RuntimeContainer container, S3DatasetProperties properties) {
        this.properties = properties;
        return ValidationResult.OK;
    }
}
