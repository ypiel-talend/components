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

package org.talend.components.mongodb.common;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;

public class MongoDBTestUtils {

    public static Schema BASIC_SCHEMA = SchemaBuilder.builder().record("Schema").fields() //
            .name("Id").type().stringType().noDefault() //
            .name("Name").type().stringType().noDefault() //
            .name("Age").type().intType().noDefault().endRecord();

    public static Schema BASIC_SCHEMA_01 = SchemaBuilder.builder().record("Schema").fields() //
            .name("Id").type().stringType().noDefault() //
            .name("Name").type().stringType().noDefault() //
            .name("Age").type().intType().noDefault()//
            .name("Note").type().stringType().noDefault() //
            .endRecord();
}
