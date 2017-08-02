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
package org.talend.components.google.table;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit-tests for {@link GoogleFusionTableProperties}
 */
public class GoogleFusionTablePropertiesTest {
    
    @Test
    public void testBuildQuery() {
        String expectedSql = "SELECT id,name,age FROM 0123456789abcd";
        
        GoogleFusionTableProperties tableProperties = new GoogleFusionTableProperties("tableProperties");
        tableProperties.init();
        
        Schema tableSchema = SchemaBuilder.builder().record("Record").fields() //
                .name("id").type().intType().noDefault() //
                .name("name").type().stringType().noDefault() //
                .name("age").type().intType().noDefault() //
                .endRecord();
        
        String tableId = "0123456789abcd";
        
        tableProperties.tableId.setValue(tableId);
        tableProperties.tableSchema.schema.setValue(tableSchema);
        
        String actualSql = tableProperties.buildQuery();
        Assert.assertEquals(expectedSql, actualSql);
    }

}
