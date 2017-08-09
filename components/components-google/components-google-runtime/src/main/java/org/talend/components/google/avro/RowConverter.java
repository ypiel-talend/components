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
package org.talend.components.google.avro;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.Schema.Type;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.IndexedRecord;
import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.avro.LogicalTypeUtils;
import org.talend.daikon.avro.SchemaConstants;
import org.talend.daikon.avro.converter.AbstractAvroConverter;
import org.talend.daikon.avro.converter.AvroConverter;
import org.talend.daikon.avro.converter.string.StringBooleanConverter;
import org.talend.daikon.avro.converter.string.StringConverter;
import org.talend.daikon.avro.converter.string.StringIntConverter;
import org.talend.daikon.avro.converter.string.StringLongConverter;
import org.talend.daikon.avro.converter.string.StringStringConverter;
import org.talend.daikon.avro.converter.string.StringTimestampConverter;

/**
 * Converts data row as List<Object> to {@link IndexedRecord} using schema to guess value type
 */
@SuppressWarnings("rawtypes")
public class RowConverter extends AbstractAvroConverter<List, IndexedRecord> {

    /**
     * Contains available {@link StringConverter}. Avro type is used as a key
     * However datum class could be also used as key. It depends on what data
     * mapping is required for particular component family. There might be
     * situations when several datum classes are mapped to the same avro type.
     * This is the case to use datum class as a key
     */
    private static final Map<Type, AvroConverter> converterRegistry;

    /**
     * Stores converters. Array index corresponds to field index
     */
    private AvroConverter[] converters;

    /**
     * Fill in converter registry
     */
    static {
        converterRegistry = new HashMap<>();
        converterRegistry.put(Type.BOOLEAN, new StringBooleanConverter());
        converterRegistry.put(Type.DOUBLE, new BigDecimalDoubleConverter());
        converterRegistry.put(Type.INT, new StringIntConverter());
        converterRegistry.put(Type.LONG, new StringLongConverter());
        converterRegistry.put(Type.STRING, new StringStringConverter());
    }

    /**
     * Constructor sets outgoing record schema and {@link List} class as datum class
     * 
     * @param clazz
     * @param schema
     */
    public RowConverter(Schema schema) {
        super(List.class, schema);
        initConverters(schema);
    }

    /**
     * Initialize converters per each schema field
     * 
     * @param schema
     * design schema
     */
    private void initConverters(Schema schema) {
        converters = new AvroConverter[schema.getFields().size()];
        List<Field> fields = schema.getFields();
        for (int i = 0; i < schema.getFields().size(); i++) {
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

    /**
     * @throws UnsupportedOperationException as this method is not supported yet
     */
    @Override
    public List<Object> convertToDatum(IndexedRecord value) {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    @Override
    public IndexedRecord convertToAvro(List row) {
        IndexedRecord record = new GenericData.Record(getSchema());
        for (int i = 0; i < row.size(); i++) {
            Object value = converters[i].convertToAvro(row.get(i));
            record.put(i, value);
        }
        return record;
    }

}
