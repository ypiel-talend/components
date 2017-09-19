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

package org.talend.components.filesystem.runtime.s3;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.talend.components.filesystem.FilesystemDatasetProperties;
import org.talend.components.filesystem.FilesystemDatastoreProperties;
import org.talend.components.simplefileio.s3.S3DatasetProperties;

import com.talend.shaded.com.amazonaws.auth.AWSCredentialsProviderChain;
import com.talend.shaded.com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.talend.shaded.com.amazonaws.services.s3.AmazonS3;
import com.talend.shaded.com.amazonaws.services.s3.AmazonS3Client;
import com.talend.shaded.org.apache.hadoop.fs.s3a.*;

public class S3Connection {

    public static AmazonS3 createClient(FilesystemDatastoreProperties properties) {
        AWSCredentialsProviderChain credentials;
        if (properties.specifyCredentials.getValue()) {
            credentials = new AWSCredentialsProviderChain(
                    new BasicAWSCredentialsProvider(properties.accessKey.getValue(), properties.secretKey.getValue()),
                    new DefaultAWSCredentialsProviderChain(), new AnonymousAWSCredentialsProvider());
        } else {
            // do not be polluted by hidden accessKey/secretKey
            credentials = new AWSCredentialsProviderChain(new DefaultAWSCredentialsProviderChain(),
                    new AnonymousAWSCredentialsProvider());
        }
        AmazonS3 conn = new AmazonS3Client(credentials);
        return conn;
    }

    public static S3AFileSystem createFileSystem(FilesystemDatasetProperties dataset) throws IOException {
        Configuration config = new Configuration(true);
        setS3Configuration(config, dataset.s3Dataset);
        setS3Configuration(config, dataset.getDatastoreProperties());
        try {
            return (S3AFileSystem) FileSystem.get(new URI(Constants.FS_S3A + "://" + dataset.s3Dataset.bucket.getValue()),
                    config);
        } catch (URISyntaxException e) {
            // The URI is constant, so this exception should never occur.
            throw new RuntimeException(e);
        }
    }

    public static String getUriPath(S3DatasetProperties properties, String path) {
        // Construct the path using the s3a schema.
        return Constants.FS_S3A + "://" + properties.bucket.getValue() + "/" + path;
    }

    public static String getUriPath(S3DatasetProperties properties) {
        return getUriPath(properties, properties.object.getValue());
    }

    public static void setS3Configuration(Configuration conf, FilesystemDatastoreProperties properties) {
        // Never reuse a filesystem created through this object.
        conf.set(String.format("fs.%s.impl.disable.cache", Constants.FS_S3A), "true");
        if (properties.specifyCredentials.getValue()) {
            // do not be polluted by hidden accessKey/secretKey
            conf.set(Constants.ACCESS_KEY, properties.accessKey.getValue());
            conf.set(Constants.SECRET_KEY, properties.secretKey.getValue());
        }
    }

    public static void setS3Configuration(Configuration conf, S3DatasetProperties properties) {
        if (properties.encryptDataAtRest.getValue()) {
            conf.set(Constants.SERVER_SIDE_ENCRYPTION_ALGORITHM, S3AEncryptionMethods.SSE_KMS.getMethod());
            conf.set(Constants.SERVER_SIDE_ENCRYPTION_KEY, properties.kmsForDataAtRest.getValue());
        }
        if (properties.encryptDataInMotion.getValue()) {
            // TODO: these don't exist yet...
            conf.set("fs.s3a.client-side-encryption-algorithm", S3AEncryptionMethods.SSE_KMS.getMethod());
            conf.set("fs.s3a.client-side-encryption-key", properties.kmsForDataInMotion.getValue());
        }
    }
}
