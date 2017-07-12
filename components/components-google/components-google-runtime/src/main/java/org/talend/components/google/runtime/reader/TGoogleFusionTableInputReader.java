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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.IndexedRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.component.runtime.AbstractBoundedReader;
import org.talend.components.api.component.runtime.Result;
import org.talend.components.common.avro.RootSchemaUtils;
import org.talend.components.common.component.runtime.RootRecordUtils;
import org.talend.components.google.tgooglefusiontableinput.TGoogleFusionTableInputProperties;
import org.talend.components.google.tgooglefusiontableinput.TGoogleFusionTableInputProperties.QueryMode;
import org.talend.daikon.avro.converter.AvroConverter;

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
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.fusiontables.Fusiontables;
import com.google.api.services.fusiontables.FusiontablesScopes;
import com.google.api.services.fusiontables.Fusiontables.Query.Sql;
import com.google.api.services.fusiontables.model.Column;
import com.google.api.services.fusiontables.model.Table;
import com.google.api.services.fusiontables.model.TableList;

/**
 * Simple implementation of a reader.
 */
public class TGoogleFusionTableInputReader extends AbstractBoundedReader<IndexedRecord> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TGoogleFusionTableInputReader.class);

    private boolean started = false;
    
    private boolean hasMore = false;

    private BufferedReader reader = null;

    private IndexedRecord current;
    
    /**
     * Converts datum field values to avro format
     */
    private AvroConverter<String, IndexedRecord> converter;
    
    /**
     * Runtime schema - schema of data record
     */
    private Schema runtimeSchema;
    
    /**
     * Root schema includes Runtime schema and schema of out of band data (a.k.a flow variables)
     */
    private Schema rootSchema;
    
    /** Directory to store user credentials. */
    private static final java.io.File DATA_STORE_DIR =
        new java.io.File(System.getProperty("C:/temp"), ".store/fusion_tables_sample");
    
    /** From Google sample:
     * Global instance of the {@link DataStoreFactory}. The best practice is to make it a single
     * globally shared instance across your application.
     */
    private static FileDataStoreFactory dataStoreFactory;

    /** From Google sample: Global instance of the HTTP transport. */
    private static HttpTransport httpTransport;
    
    /** From Google sample: Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    
    private static Fusiontables fusiontables;
    
    /**
     * Be sure to specify the name of your application. If the application name is {@code null} or
     * blank, the application will log a warning. Suggested format is "MyCompany-ProductName/1.0".
     */
    private static final String APPLICATION_NAME = "";

	private static final String String = null;


   
    
    
    /**
     * Holds values for return properties
     */
    private Result result;

    public TGoogleFusionTableInputReader(TGoogleFusionTableInputSource source) {
        super(source);
    }

    
    /** From google sample: Authorizes the installed application to access user's protected data. */
    public static Credential authorize() throws Exception {
      // load client secrets
 //     GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new StringReader(TGoogleFusionTableInputProperties.clientSecret.getValue()));
    	GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new StringReader("{\"installed\":{\"client_id\":\"693143298047-pvpsf3g5edd5nt8hbkarajbr0qrs3jkt.apps.googleusercontent.com\",\"project_id\":\"engineering-152721\",\"auth_uri\":\"https://accounts.google.com/o/oauth2/auth\",\"token_uri\":\"https://accounts.google.com/o/oauth2/token\",\"auth_provider_x509_cert_url\":\"https://www.googleapis.com/oauth2/v1/certs\",\"client_secret\":\"J6XHohVj6idt4ktvTQt2ltcK\",\"redirect_uris\":[\"urn:ietf:wg:oauth:2.0:oob\",\"http://localhost\"]}}")); 
    	
    	// set up authorization code flow
    	    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
    	        httpTransport, JSON_FACTORY, clientSecrets,
    	        Collections.singleton(FusiontablesScopes.FUSIONTABLES)).setDataStoreFactory(
    	        dataStoreFactory).build();
    	    // authorize
    	    return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize(null);
    	  }
     
    
    public static void main(String[] args) {
        try {
          httpTransport = GoogleNetHttpTransport.newTrustedTransport();
          dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
          // authorization
          Credential credential = authorize();
          // set up global FusionTables instance
          fusiontables = new Fusiontables.Builder(
              httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
          // run commands
          if (TGoogleFusionTableInputProperties.resourceType.getValue() == QueryMode.Table) {
              retrieveTable();
          }
          if (TGoogleFusionTableInputProperties.resourceType.getValue() == QueryMode.Column) {
              retrieveColumn();
          }
          if (TGoogleFusionTableInputProperties.resourceType.getValue() == QueryMode.Row) {
              showRows();
          }
          
          
          // success!
          return;
        } catch (IOException e) {
          System.err.println(e.getMessage());
        } catch (Throwable t) {
          t.printStackTrace();
        }
        System.exit(1);
      }
    
    /** Retrieve table for the authenticated user. */
    public static void retrieveTable() throws IOException {

      // Fetch the table
    	Fusiontables.Table.Get retrievedTable = fusiontables.table().get(TGoogleFusionTableInputProperties.fusionTable.getValue());
    
      Table table = retrievedTable.execute();

      if (table.getTableId() == null || table.getTableId().isEmpty()) {
        System.out.println("No table found!");
        return;
      }

    }
    
    /** Retrieve table for the authenticated user. */
    public static void retrieveColumn() throws IOException {

      // Fetch the table
    	Fusiontables.Column.Get retrievedColumn = fusiontables.column().get(TGoogleFusionTableInputProperties.fusionTable.getValue(), TGoogleFusionTableInputProperties.fusionColumn.getValue());
    
      Column column = retrievedColumn.execute();
      
      if (fusiontables.table().get(TGoogleFusionTableInputProperties.fusionTable.getValue()) == null || fusiontables.table().get(TGoogleFusionTableInputProperties.fusionTable.getValue()).isEmpty()) {
          System.out.println("No such table found!");
          return;
        }

      if (column.getColumnId() == null) {
        System.out.println("No such column found!");
        return;
      }

    }
    
    /**
     * @param tableId
     * @throws IOException
     */
    private static void showRows() throws IOException {

      Sql sql = fusiontables.query().sql(TGoogleFusionTableInputProperties.fusionQuery.getValue());

      try {
        sql.execute();
      } catch (IllegalArgumentException e) {
        // For google-api-services-fusiontables-v1-rev1-1.7.2-beta this exception will always
        // been thrown.
        // Please see issue 545: JSON response could not be deserialized to Sqlresponse.class
        // http://code.google.com/p/google-api-java-client/issues/detail?id=545
      }
    }
      
 @Override
    public boolean start() throws IOException {
  //      reader = new BufferedReader(new FileReader(getCurrentSource().getFilePath()));
  //  result = new Result();
   //     LOGGER.debug("open: " + getCurrentSource().getFilePath()); //$NON-NLS-1$
        started = true;
        return advance();
    }

    @Override
    public boolean advance() throws IOException {
        if (!started) {
            throw new IllegalStateException("Reader wasn't started");
        }
        hasMore = reader.ready();
        if (hasMore) {
            String line = reader.readLine();
            // create the data schema
            Schema dataSchema = getRuntimeSchema(line);
            // create the data IndexRecord
            IndexedRecord dataRecord = getConverter(dataSchema).convertToAvro(line);
            // create the outOfBand record (since the schema is static)
            IndexedRecord outOfBandRecord = new GenericData.Record(TGoogleFusionTableInputProperties.outOfBandSchema);
            outOfBandRecord.put(0, result.totalCount);
            // create the root record
            current = RootRecordUtils.createRootRecord(getRootSchema(), dataRecord, outOfBandRecord);
            result.totalCount++;
        }
        return hasMore;
    }

    @Override
    public IndexedRecord getCurrent() throws NoSuchElementException {
        if (!started) {
            throw new NoSuchElementException("Reader wasn't started");
        }
        if (!hasMore) {
            throw new NoSuchElementException("Has no more elements");
        }
        return current;
    }
/**
    @Override
    public void close() throws IOException {
        if (!started) {
            throw new IllegalStateException("Reader wasn't started");
        }
        reader.close();
        LOGGER.debug("close: " + getCurrentSource().getFilePath()); //$NON-NLS-1$
        reader = null;
        started = false;
        hasMore = false;
    }
*/
    /**
     * Returns values of Return properties. It is called after component finished his work (after {@link this#close()} method)
     */
    @Override
    public Map<String, Object> getReturnValues() {
        return result.toMap();
    }
    
    @Override
    public TGoogleFusionTableInputSource getCurrentSource() {
        return (TGoogleFusionTableInputSource) super.getCurrentSource();
    }

    /**
     * Returns implementation of {@link AvroConverter}, creates it if it doesn't
     * exist.
     * 
     * @param runtimeSchema
     *            Schema of data record
     * @return converter
     */
    private AvroConverter<String, IndexedRecord> getConverter(Schema runtimeSchema) {
        if (converter == null) {
            converter = getCurrentSource().createConverter(runtimeSchema);
        }
        return converter;
    }
    
    /**
     * Returns Runtime schema, which is used during data IndexedRecord creation
     * Creates the schema only once for all records
     * 
     * @param delimitedString delimited line, which was read from file
     * @return avro Runtime schema
     */
    private Schema getRuntimeSchema(String delimitedString) {
        if (runtimeSchema == null) {
            runtimeSchema = getCurrentSource().provideRuntimeSchema(delimitedString);
        }
        return runtimeSchema;
    }
    
    /**
     * Returns Root schema, which is used during IndexedRecord creation <br>
     * This should be called only after {@link this#getRuntimeSchema(String)} is
     * called
     * 
     * @return avro Root schema
     */
    private Schema getRootSchema() {
        if (rootSchema == null) {
            if (runtimeSchema == null) {
                throw new IllegalStateException("Runtime schema should be created before Root schema");
            } else {
                rootSchema = RootSchemaUtils.createRootSchema(runtimeSchema, TGoogleFusionTableInputProperties.outOfBandSchema);
            }
        }
        return rootSchema;
    }
}
