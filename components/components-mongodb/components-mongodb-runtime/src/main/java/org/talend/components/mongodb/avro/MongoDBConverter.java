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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.Schema.Type;
import org.apache.avro.generic.IndexedRecord;
import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.avro.LogicalTypeUtils;
import org.talend.daikon.avro.SchemaConstants;
import org.talend.daikon.avro.converter.AvroConverter;
import org.talend.daikon.avro.converter.string.StringBooleanConverter;
import org.talend.daikon.avro.converter.string.StringConverter;
import org.talend.daikon.avro.converter.string.StringDoubleConverter;
import org.talend.daikon.avro.converter.string.StringFloatConverter;
import org.talend.daikon.avro.converter.string.StringIntConverter;
import org.talend.daikon.avro.converter.string.StringLongConverter;
import org.talend.daikon.avro.converter.string.StringStringConverter;
import org.talend.daikon.avro.converter.string.StringTimestampConverter;

public class MongoDBConverter implements AvroConverter<String, IndexedRecord> {

    /**
     * Contains available {@link StringConverter}. Avro type is used as a key
     * However datum class could be also used as key. It depends on what data
     * mapping is required for particular component family. There might be
     * situations when several datum classes are mapped to the same avro type.
     * This is the case to use datum class as a key
     */
    private static final Map<Type, StringConverter> converterRegistry;

    /**
     * Fill in converter registry
     */
    static {
        converterRegistry = new HashMap<>();
        converterRegistry.put(Type.BOOLEAN, new StringBooleanConverter());
        converterRegistry.put(Type.DOUBLE, new StringDoubleConverter());
        converterRegistry.put(Type.FLOAT, new StringFloatConverter());
        converterRegistry.put(Type.INT, new StringIntConverter());
        converterRegistry.put(Type.LONG, new StringLongConverter());
        converterRegistry.put(Type.STRING, new StringStringConverter());
    }

    /**
     * Schema of Avro IndexedRecord
     */
    private final Schema schema;

    /**
     * Number of fields in schema
     */
    private final int size;

    /**
     * Stores converters. Index in array corresponds to index of field in
     * schema(?)
     */
    private StringConverter[] converters;

    /**
     * Constructor sets schema and delimiter, which will be used during
     * conversion
     *
     * @param schema
     * avro schema
     */
    public MongoDBConverter(Schema schema) {
        this.schema = schema;
        this.size = schema.getFields().size();
        initConverters(schema);
    }

    /**
     * Initialize converters per each schema field
     * 
     * @param schema
     * design schema
     */
    private void initConverters(Schema schema) {
        converters = new StringConverter[size];
        List<Field> fields = schema.getFields();
        for (int i = 0; i < size; i++) {
            Field field = fields.get(i);
            Schema fieldSchema = field.schema();
            fieldSchema = AvroUtils.unwrapIfNullable(fieldSchema);
            if (LogicalTypeUtils.isLogicalTimestampMillis(fieldSchema)) {
                String datePattern = field.getProp(SchemaConstants.TALEND_COLUMN_PATTERN);
                converters[i] = new StringTimestampConverter(datePattern);
            } else {
                Type type = fieldSchema.getType();
                converters[i] = converterRegistry.get(type);
            }
        }
    }

    @Override
    public IndexedRecord convertToAvro(String value) {
        // TODO

        return null;
    }

    @Override
    public String convertToDatum(IndexedRecord record) {
        if (!schema.equals(record.getSchema())) {
            throw new IllegalArgumentException("Input record has different schema");
        }
        if (size == 0) {
            return "";
        }
        // TODO

        return null;
    }

    /**
     * Returns datum class, which is String
     * 
     * @return String.class
     */
    @Override
    public Class<String> getDatumClass() {
        return String.class;
    }

    /**
     * Returns avro schema
     * 
     * @return avro schema
     */
    @Override
    public Schema getSchema() {
        return schema;
    }

}
