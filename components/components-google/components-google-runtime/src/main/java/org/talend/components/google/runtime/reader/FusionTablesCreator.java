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
package org.talend.components.google.runtime.reader;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.google.api.services.fusiontables.Fusiontables;
import com.google.api.services.fusiontables.FusiontablesScopes;

/**
 * Creates instance of {@link Fusiontables}
 */
class FusionTablesCreator {

    /**
     * Application name required by Google Fusion Tables API.
     * It doesn't affect on connection
     */
    private static final String APP_NAME = "GoogleFusionTablesComponent";

    private final String clientId;

    private final String clientSecret;

    /**
     * Constructor sets required credentials and storage path
     * 
     * @param clientId
     * @param clientSecret
     */
    FusionTablesCreator(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    /**
     * Creates {@link Fusiontables} instance
     * 
     * @return {@link Fusiontables} instance
     * @throws GeneralSecurityException
     * @throws IOException
     */
    Fusiontables createFusionTables() throws GeneralSecurityException, IOException {
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        DataStoreFactory dataStoreFactory = createDataStoreFactory();
        Credential credential = authorize(httpTransport, jsonFactory, dataStoreFactory);
        Fusiontables fusionTables = new Fusiontables.Builder(httpTransport, jsonFactory, credential).setApplicationName(APP_NAME)
                .build();
        return fusionTables;
    }

    /**
     * Creates storage for received access and refresh tokens. Next time connection won't require user consent.
     * It will reuse tokens stored in the storage
     * 
     * @return
     * @throws IOException
     */
    private DataStoreFactory createDataStoreFactory() throws IOException {
        return MemoryDataStoreFactory.getDefaultInstance();
    }

    /**
     * Creates flow to get authorization code and tokens:
     * Starts server to get authorization code from Google server
     * Tries to open system browser for user's consent
     * 
     * @param httpTransport
     * @param jsonFactory
     * @param dataStoreFactory
     * @return
     * @throws IOException
     */
    private Credential authorize(HttpTransport httpTransport, JsonFactory jsonFactory, DataStoreFactory dataStoreFactory)
            throws IOException {
        GoogleClientSecrets clientSecrets = createClientSecret();
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, clientSecrets,
                Collections.singleton(FusiontablesScopes.FUSIONTABLES)).setDataStoreFactory(dataStoreFactory).build();
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize(clientId);
    }

    /**
     * Creates client secret instance from client id and client secret
     * 
     * @return GoogleClientSecrets
     */
    private GoogleClientSecrets createClientSecret() {
        GoogleClientSecrets.Details details = new GoogleClientSecrets.Details();
        details.setClientId(clientId);
        details.setClientSecret(clientSecret);
        GoogleClientSecrets clientSecrets = new GoogleClientSecrets();
        clientSecrets.setInstalled(details);
        return clientSecrets;
    }

}
