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
import java.io.StringReader;
import java.util.Collections;
import java.util.List;

import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.io.AvroIO;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.hdfs.HadoopFileSystemOptions;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CommonConfigurationKeys;
import org.apache.hadoop.security.UserGroupInformation;
import org.talend.components.adapter.beam.BeamJobRuntimeContainer;
import org.talend.components.api.component.runtime.RuntimableRuntime;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.filesystem.FilesystemDatasetProperties;
import org.talend.components.filesystem.FilesystemDatastoreProperties;
import org.talend.components.filesystem.input.FileSystemInputProperties;
import org.talend.components.filesystem.runtime.s3.S3Connection;
import org.talend.components.simplefileio.SimpleFileIOFormat;
import org.talend.daikon.avro.converter.IndexedRecordConverter;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.exception.error.CommonErrorCodes;
import org.talend.daikon.properties.ValidationResult;

public class FilesystemInputRuntime extends PTransform<PBegin, PCollection<IndexedRecord>>
        implements RuntimableRuntime<FileSystemInputProperties> {

    private FileSystemInputProperties properties = null;

    private PipelineOptions pipelineOptions = null;

    @Override
    public ValidationResult initialize(RuntimeContainer container, FileSystemInputProperties properties) {
        this.properties = properties;
        Object pipelineOptionsObj = container.getGlobalData(BeamJobRuntimeContainer.PIPELINE_OPTIONS);
        if (pipelineOptionsObj != null) {
            this.pipelineOptions = (PipelineOptions) pipelineOptionsObj;
        }
        return ValidationResult.OK;
    }

    @Override
    public PCollection<IndexedRecord> expand(PBegin input) {
        FilesystemDatasetProperties datasetProperties = properties.getDatasetProperties();
        FilesystemDatastoreProperties datastoreProperties = datasetProperties.getDatastoreProperties();

        String path = null;
        switch (datastoreProperties.filesystemType.getValue()) {
        case HDFS:
            Configuration hdfsConf = new Configuration(true);
            List<Configuration> hdfsConfiguration = pipelineOptions.as(HadoopFileSystemOptions.class).getHdfsConfiguration();
            if (hdfsConfiguration != null && hdfsConfiguration.size() > 0) {
                hdfsConf = hdfsConfiguration.get(0);
                String hdfsSchema = hdfsConf.get(CommonConfigurationKeys.FS_DEFAULT_NAME_KEY);
                path = hdfsSchema + datasetProperties.simpleDataset.path.getValue();
            } else {
                path = datasetProperties.simpleDataset.path.getValue();
            }
            UserGroupInformation.setConfiguration(hdfsConf);
            if (datastoreProperties.useKerberos.getValue()) {
                try {
                    UserGroupInformation.loginUserFromKeytab(datastoreProperties.kerberosPrincipal.getValue(),
                            datastoreProperties.kerberosKeytab.getValue());
                } catch (IOException e) {
                    throw TalendRuntimeException.createUnexpectedException(e);
                }
            } else {
                String userName = datastoreProperties.userName.getValue();
                if (userName != null) {
                    UserGroupInformation.setLoginUser(UserGroupInformation.createRemoteUser(userName));
                }
            }
            break;
        case S3:
            Configuration s3Conf = new Configuration(false);
            S3Connection.setS3Configuration(s3Conf, datasetProperties.s3Dataset);
            S3Connection.setS3Configuration(s3Conf, datastoreProperties);
            pipelineOptions.as(HadoopFileSystemOptions.class).setHdfsConfiguration(Collections.singletonList(s3Conf));
            path = S3Connection.getUriPath(datasetProperties.s3Dataset);
            break;
        case GCS:
            path = datasetProperties.simpleDataset.path.getValue();
            break;
        default:
            throw TalendRuntimeException.createUnexpectedException(
                    String.format("Do not support this filesystem: %s", datastoreProperties.filesystemType.getValue()));
        }

        SimpleFileIOFormat format = null;
        switch (datastoreProperties.filesystemType.getValue()) {
        case S3:
            format = datasetProperties.s3Dataset.format.getValue();
            break;
        case GCS:
        case HDFS:
        default:
            format = datasetProperties.simpleDataset.format.getValue();
        }

        switch (format) {
        case CSV:
            String fd = datasetProperties.simpleDataset.getFieldDelimiter();
            if (fd.length() > 1) {
                fd = fd.trim();
            }
            if (fd.isEmpty())
                TalendRuntimeException.build(CommonErrorCodes.UNEXPECTED_ARGUMENT).setAndThrow("single character field delimiter",
                        fd);
            PCollection<String> lines = input.apply(TextIO.read().from(path));
            return lines.apply(ParDo.of(new ExtractCsvRecord<>(fd.charAt(0))));
        case AVRO:
            AvroIO.
            break;
        case PARQUET:
            break;
        default:
            throw TalendRuntimeException.createUnexpectedException(String.format("Do not support this format: %s", format));
        }

        return null;
    }

    public static class ExtractCsvRecord<T> extends DoFn<T, IndexedRecord> {

        static {
            // Ensure that the singleton for the CsvAvroRegistry is created.
            CsvAvroRegistry.get();
        }

        public final char fieldDelimiter;

        /** The converter is cached for performance. */
        private transient IndexedRecordConverter<CSVRecord, ? extends IndexedRecord> converter;

        public ExtractCsvRecord(char fieldDelimiter) {
            this.fieldDelimiter = fieldDelimiter;
        }

        @ProcessElement
        public void processElement(ProcessContext c) throws IOException {
            if (converter == null) {
                converter = new CsvAvroRegistry.CsvRecordToIndexedRecordConverter();
            }
            String in = c.element().toString();
            for (CSVRecord r : CSVFormat.RFC4180.withDelimiter(fieldDelimiter).parse(new StringReader(in)))
                c.output(converter.convertToAvro(r));
        }
    }
}
