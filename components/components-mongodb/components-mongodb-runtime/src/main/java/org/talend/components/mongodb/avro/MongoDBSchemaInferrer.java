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
package org.talend.components.mongodb.avro;

import static org.talend.components.mongodb.runtime.MongoDBSourceOrSink.COUNT_ROWS;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.SchemaBuilder;
import org.talend.components.mongodb.runtime.MongoDBRuntimeHelper;
import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.avro.SchemaConstants;
import org.talend.daikon.avro.inferrer.SchemaInferrer;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * Creates (infers) {@link Schema} from data, which is read from data storage
 * This is used in case user specifies dynamic field in Design schema
 */
public class MongoDBSchemaInferrer implements SchemaInferrer<DBCollection> {

    private static final MongoDBSchemaInferrer inferrer = new MongoDBSchemaInferrer();

    public static MongoDBSchemaInferrer get() {
        return inferrer;
    }

    /**
     * Default schema for dynamic fields are of type String
     */
    private static final Schema STRING_SCHEMA = AvroUtils._string();

    /**
     * Creates Runtime schema from incoming data. <br>
     * Schema is created in following way: <br>
     * 1. Delimited string is splitted using <code>delimiter</code> to count
     * number of fields in delimited string <br>
     * 2. The same number of fields are created for Runtime schema <br>
     * 3. Field names are {@code "column<Index>"} <br>
     * 4. Field types are String
     *
     * @param collection a line, which was read from file source
     * @return Runtime avro schema
     */
    @Override
    public Schema inferSchema(DBCollection collection) {
        List<String> existColumnNames = new ArrayList<String>();
        Set<String> indexColNames = new HashSet<String>();
        List<DBObject> dbObjs = collection.getIndexInfo();
        for (DBObject dbObj : dbObjs) {
            indexColNames.addAll(dbObj.keySet());
        }
        DBCursor dbCursor = collection.find();
        dbCursor.limit(COUNT_ROWS);
        List<Field> fields = new ArrayList<>();
        while (dbCursor.hasNext()) {
            DBObject dbObj = dbCursor.next();
            Set<String> columnNames = dbObj.keySet();
            for (String colName : columnNames) {
                colName = MongoDBRuntimeHelper.validateColumnName(colName);
                if (existColumnNames.contains(colName)) {
                    continue;
                }
                existColumnNames.add(colName);
                Object value = dbObj.get(colName);

                boolean isKey = false;
                if (indexColNames.contains(colName)) {
                    isKey = true;
                }
                // FIXME need to fix special field name like "a.b" "a/b"
                Schema.Field field = mongoTypce2Avro(colName, value, isKey);

                fields.add(field);
            }
        }
        return Schema.createRecord(collection.getName(), null, null, false, fields);
    }

    /**
     * Guess field type by the field value
     */
    private Schema.Field mongoTypce2Avro(String name, Object value, boolean isKey) {
        Field field = null;
        Schema schema = null;
        String className = value.getClass().getSimpleName();
        switch (className) {
        case "String":
            schema = STRING_SCHEMA;
            break;
        case "Integer":
            schema = AvroUtils._int();
            break;
        case "Decimal":
            schema = AvroUtils._decimal();
            break;
        case "Double":
            schema = AvroUtils._double();
            break;
        case "Float":
            schema = AvroUtils._float();
            break;
        case "Date":
            schema = AvroUtils._logicalTimestamp();
            break;
        case "Boolean":
            schema = AvroUtils._boolean();
            break;
        default:
            schema = STRING_SCHEMA;
            break;
        }
        field = wrap(schema, name, !isKey);
        if (AvroUtils._logicalTimestamp().equals(schema)) {
            // FIXME need to confirm the pattern
            field.addProp(SchemaConstants.TALEND_COLUMN_PATTERN, "yyyy-MM-dd");
        }
        if (isKey) {
            field.addProp(SchemaConstants.TALEND_COLUMN_IS_KEY, true);
        }

        return field;
    }

    protected Field wrap(Schema base, String name, boolean nullable) {
        Schema schema = nullable ? SchemaBuilder.builder().nullable().type(base) : base;
        return new Field(name, schema, null, (Object) null);
    }

}
