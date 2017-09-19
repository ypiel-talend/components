package org.talend.components.mongodb.runtime;

import static org.talend.components.common.ComponentConstants.KEY_CONNECTION;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.avro.Schema;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.mongodb.MongoDBConnectionProperties;
import org.talend.components.mongodb.MongoDBRuntimeSourceOrSink;
import org.talend.components.mongodb.avro.MongoDBSchemaInferrer;
import org.talend.daikon.NamedThing;
import org.talend.daikon.SimpleNamedThing;
import org.talend.daikon.i18n.GlobalI18N;
import org.talend.daikon.i18n.I18nMessages;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.ValidationResultMutable;
import org.talend.daikon.properties.property.Property;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

public class MongoDBSourceOrSink implements MongoDBRuntimeSourceOrSink {

    protected static final I18nMessages MESSAGES = GlobalI18N.getI18nMessageProvider().getI18nMessages(MongoDBSourceOrSink.class);

    private transient static final Logger LOGGER = LoggerFactory.getLogger(MongoDBSourceOrSink.class);

    // The max number of retrieved records which used to guess schema of the collection
    public static final int COUNT_ROWS = 50;

    public static final String KEY_MONGO = "mongo";

    public static final String KEY_DB = "db";

    private static final String DB_COLLECTION_MARK = "$";

    protected MongoDBConnectionProperties properties;

    protected Mongo connect(RuntimeContainer container) throws Exception {

        Mongo mongo = null;

        String refComponentId = properties.getReferencedComponentId();
        // Using another component's connection
        if (refComponentId != null) {
            // In a runtime container
            if (container != null) {
                mongo = (Mongo) container.getComponentData(refComponentId, KEY_CONNECTION);
                if (mongo != null) {
                    return mongo;
                }
                throw new IOException(MESSAGES.getMessage("error.refComponentNotConnected", refComponentId));
            }
            // Design time
            properties = properties.getReferencedConnectionProperties();
            // FIXME This should not happen - but does as of now
            if (properties == null)
                throw new IOException(MESSAGES.getMessage("error.refComponentWithoutProperties", refComponentId));
        }

        MongoClientOptions clientOptions = new MongoClientOptions.Builder().build();
        List<MongoCredential> mongoCredentialList = getCredential(properties);

        ServerAddress serverAddress = new ServerAddress(properties.host.getValue(), properties.port.getValue());
        mongo = new MongoClient(serverAddress, mongoCredentialList, clientOptions);
        mongo.getAddress();

        if (container != null) {
            container.setComponentData(container.getCurrentComponentId(), KEY_MONGO, mongo);
            if (!StringUtils.isEmpty(properties.database.getValue())) {
                DB db = mongo.getDB(properties.database.getValue());
                container.setComponentData(container.getCurrentComponentId(), KEY_DB, db);
            }
        }

        return mongo;
    }

    @Override
    public List<NamedThing> getSchemaNames(RuntimeContainer container) throws IOException {
        // Returns the list with a table names (for the wh, db and schema)
        try {
            DB db = getDatabase(container);
            List<NamedThing> returnList = new ArrayList<>();
            // Fetch all tables in the db and schema provided
            Set<String> collections = db.getCollectionNames();
            for (String collectionName : collections) {
                returnList.add(new SimpleNamedThing(collectionName, collectionName));
            }
            return returnList;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    /**
     * Get all database names
     */
    @Override
    public List<NamedThing> getDatabaseNames(RuntimeContainer container) throws IOException {
        // Returns the list with a database names
        List<NamedThing> returnList = new ArrayList<>();
        try {
            Mongo mongo = connect(container);
            List<String> dbNames = mongo.getDatabaseNames();
            // Fetch all database
            LOGGER.debug("Exist databases:");
            for (String dbName : dbNames) {
                returnList.add(new SimpleNamedThing(dbName, dbName));
                LOGGER.debug(dbName);
            }
            return returnList;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public List<NamedThing> getCollectionNames(RuntimeContainer container, List<NamedThing> databases) throws IOException {
        List<NamedThing> returnList = new ArrayList<>();
        try {
            Mongo mongo = connect(container);
            for (NamedThing dbName : databases) {
                Set<String> collectionNames = mongo.getDB(dbName.getName()).getCollectionNames();
                for (String collectionName : collectionNames) {
                    String collectionNameWithDB = getCollectionNameWithDB(dbName.getName(), collectionName);
                    returnList.add(new SimpleNamedThing(collectionNameWithDB, collectionNameWithDB));
                }
            }
        } catch (Exception e) {
            throw new IOException(e);
        }
        return returnList;
    }

    @Override
    public Map<String, List<String>> getDBCollectionMapping(Property<List<NamedThing>> selectedCollectionNames) {
        Map<String, List<String>> dbCollectionMapping = new HashMap<>();
        List<NamedThing> collectionNames = selectedCollectionNames.getValue();
        for (NamedThing collectionName : collectionNames) {
            String collectionNameWithDB = collectionName.getName();
            String[] names = collectionNameWithDB.split("\\" + DB_COLLECTION_MARK);
            List<String> collections = dbCollectionMapping.get(names[0]);
            if (collections != null) {
                if (!collections.contains(names[1])) {
                    dbCollectionMapping.get(names[0]).add(names[1]);
                }
            } else {
                collections = new ArrayList<>();
                collections.add(names[1]);
                dbCollectionMapping.put(names[0], collections);
            }
        }
        return dbCollectionMapping;
    }

    /**
     * Get schema of the collection
     */
    @Override
    public Schema getEndpointSchema(RuntimeContainer container, String collectionName) throws IOException {
        try {

            // Returns the list with a table names (for the wh, db and schema)
            DB db = getDatabase(container);
            // build schema
            List<String> existColumnNames = new ArrayList<String>();
            DBCollection collection = db.getCollection(collectionName);
            return MongoDBSchemaInferrer.get().inferSchema(collection);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public ValidationResult validate(RuntimeContainer container) {
        try {
            connect(container);
        } catch (Exception e) {
            ValidationResultMutable vr = new ValidationResultMutable();
            vr.setMessage(e.getMessage());
            vr.setStatus(ValidationResult.Result.ERROR);
            return vr;
        }
        ValidationResultMutable vr = new ValidationResultMutable();
        vr.setStatus(ValidationResult.Result.OK);
        vr.setMessage(MESSAGES.getMessage("connection.success"));
        return vr;
    }

    @Override
    public ValidationResult initialize(RuntimeContainer container, ComponentProperties properties) {
        this.properties = (MongoDBConnectionProperties) properties;
        return ValidationResult.OK;
    }

    /**
     * Get the database instance based on setting of database of connection properties
     */

    private DB getDatabase(RuntimeContainer container) throws Exception {
        Mongo mongo = connect(container);
        DB db = null;
        if (container != null) {
            db = (DB) container.getComponentData(container.getCurrentComponentId(), KEY_DB);
            if (db == null) {
                throw new IOException(MESSAGES.getMessage("error.db.unshared", container.getCurrentComponentId()));
            }
        } else {
            db = mongo.getDB(properties.database.getValue());
        }
        return db;
    }

    private String getCollectionNameWithDB(String dbName, String collectionName) {
        return dbName + DB_COLLECTION_MARK + collectionName;
    }

    private List<MongoCredential> getCredential(MongoDBConnectionProperties properties) {
        List<MongoCredential> mongoCredentialList = new ArrayList<MongoCredential>();
        if (properties.requiredAuthentication.getValue()) {
            MongoCredential mongoCredential = null;
            if ((MongoDBConnectionProperties.AuthenticationMechanism.NEGOTIATE_MEC
                    .equals(properties.authenticationMechanism.getValue()))
                    || (MongoDBConnectionProperties.AuthenticationMechanism.PLAIN_MEC
                            .equals(properties.authenticationMechanism.getValue()))
                    || (MongoDBConnectionProperties.AuthenticationMechanism.SCRAMSHA1_MEC
                            .equals(properties.authenticationMechanism.getValue()))) {
                String userId = properties.userPassword.userId.getName();
                String password = properties.userPassword.password.getName();
                if (userId == null || password == null) {
                    throw new IllegalArgumentException("Please check user password!");
                }
                if ((MongoDBConnectionProperties.AuthenticationMechanism.NEGOTIATE_MEC
                        .equals(properties.authenticationMechanism.getValue()))) {
                    if (MongoDBConnectionProperties.DBVersion.MONGODB_3_0_X.equals(properties.dbVersion.getValue())
                            || MongoDBConnectionProperties.DBVersion.MONGODB_3_2_X.equals(properties.dbVersion.getValue())) {
                        mongoCredential = com.mongodb.MongoCredential.createCredential(userId,
                                properties.authenticationDatabase.getValue(), password.toCharArray());
                    } else {
                        mongoCredential = com.mongodb.MongoCredential.createMongoCRCredential(userId,
                                properties.authenticationDatabase.getValue(), password.toCharArray());
                    }
                } else if (MongoDBConnectionProperties.AuthenticationMechanism.PLAIN_MEC
                        .equals(properties.authenticationMechanism.getValue())) {
                    mongoCredential = com.mongodb.MongoCredential.createPlainCredential(userId, "$external",
                            password.toCharArray());
                } else if (MongoDBConnectionProperties.AuthenticationMechanism.SCRAMSHA1_MEC
                        .equals(properties.authenticationMechanism.getValue())) {
                    mongoCredential = com.mongodb.MongoCredential.createScramSha1Credential(userId,
                            properties.authenticationDatabase.getValue(), password.toCharArray());
                }
            } else {
                System.setProperty("java.security.krb5.realm", properties.kerberos.realm.getValue());
                System.setProperty("java.security.krb5.kdc", properties.kerberos.kdcServer.getValue());
                System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");
                mongoCredential = com.mongodb.MongoCredential
                        .createGSSAPICredential(properties.kerberos.userPrincipal.getValue());
            }
            mongoCredentialList.add(mongoCredential);
        }
        return mongoCredentialList;
    }

}
