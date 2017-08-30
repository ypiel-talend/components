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

import org.apache.avro.Schema;
import org.talend.daikon.avro.AvroUtils;

/**
 * Creates (infers) {@link Schema} from data, which is read from data storage
 * This is used in case user specifies dynamic field in Design schema
 */
public class MongoDBSchemaInferrer {

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
     * @param delimitedString a line, which was read from file source
     * @return Runtime avro schema
     */
    public Schema inferSchema(Object delimitedString) {
        // TODO
        return null;
    }

}
