// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.processing.runtime.pythonrow;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.transforms.DoFnTester;
import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.components.processing.definition.pythonrow.MapType;
import org.talend.components.processing.definition.pythonrow.PythonRowProperties;
import org.talend.daikon.avro.GenericDataRecordHelper;
import org.talend.daikon.properties.ValidationResult;

public class PythonRowDoFnTest {

    private static IndexedRecord inputIndexedRecord = null;

    private static IndexedRecord outputIndexedRecord = null;

    private static String utf8Sample = "Les naïfs ægithales hâtifs pondant à Noël où il gèle sont sûrs d'être "
            + "déçus en voyant leurs drôles d'œufs abîmés.";

    @BeforeClass
    public static void setUp() throws IOException {
        Object[] inputAsObject1 = new Object[] { "rootdata",
                new Object[] { "subdata", new Object[] { "subsubdata1", 28, 42l }, "subdata2" } };
        Schema inputSchema = GenericDataRecordHelper.createSchemaFromObject("MyRecord", inputAsObject1);
        inputIndexedRecord = GenericDataRecordHelper.createRecord(inputAsObject1);

        Object[] inputAsObject2 = new Object[] { "rootdata2",
                new Object[] { "subdatabefore", new Object[] { "subsubdatabefore", 33, 55l }, "subdataend" } };
        outputIndexedRecord = GenericDataRecordHelper.createRecord(inputAsObject2);
    }

    @Test
    public void test_NullInput() throws Exception {

        PythonRowProperties properties = new PythonRowProperties("test");
        properties.init();
        properties.pythonCode.setValue("outputList.append(input)");
        PythonRowDoFn function = new PythonRowDoFn();
        assertEquals(ValidationResult.OK, function.initialize(null, properties));
        DoFnTester<IndexedRecord, IndexedRecord> fnTester = DoFnTester.of(function);
        List<IndexedRecord> outputs = fnTester.processBundle((IndexedRecord) null);
        assertEquals(0, outputs.size());
    }

    @Test
    public void test_Map_doNothing() throws Exception {

        PythonRowProperties properties = new PythonRowProperties("test");
        properties.init();
        properties.mapType.setValue(MapType.MAP);
        properties.pythonCode.setValue("output = input");
        PythonRowDoFn function = new PythonRowDoFn();
        assertEquals(ValidationResult.OK, function.initialize(null, properties));
        DoFnTester<IndexedRecord, IndexedRecord> fnTester = DoFnTester.of(function);
        List<IndexedRecord> outputs = fnTester.processBundle(inputIndexedRecord);
        assertEquals(1, outputs.size());

        GenericRecord outputRecord = (GenericRecord) outputs.get(0);
        compareRecords(inputIndexedRecord, outputRecord);
    }

    @Test
    public void test_Map_ApplyATransformation() throws Exception {

        PythonRowProperties properties = new PythonRowProperties("test");
        properties.init();
        properties.mapType.setValue(MapType.MAP);

        StringBuilder sb = new StringBuilder();
        sb.append("output = input\n");
        sb.append("output['a1'] = \"rootdata2\"\n");
        sb.append("output['B']['b1'] = \"subdatabefore\"\n");
        sb.append("output['B']['C']['c1'] = \"subsubdatabefore\"\n");
        sb.append("output['B']['C']['c2'] = 33\n");
        sb.append("output['B']['C']['c3'] = 55l\n");
        sb.append("output['B']['b2'] = \"subdataend\"\n");
        properties.pythonCode.setValue(sb.toString());
        PythonRowDoFn function = new PythonRowDoFn();
        assertEquals(ValidationResult.OK, function.initialize(null, properties));
        DoFnTester<IndexedRecord, IndexedRecord> fnTester = DoFnTester.of(function);
        List<IndexedRecord> outputs = fnTester.processBundle(inputIndexedRecord);
        assertEquals(1, outputs.size());

        GenericRecord outputRecord = (GenericRecord) outputs.get(0);
        compareRecords(outputIndexedRecord, outputRecord);
    }

    @Test
    public void test_Map_GenerateSchemaFromScratch() throws Exception {

        PythonRowProperties properties = new PythonRowProperties("test");
        properties.init();
        properties.mapType.setValue(MapType.MAP);

        StringBuilder sb = new StringBuilder();
        sb.append("output['a1'] = \"rootdata2\"\n");
        sb.append("output['B'] = json.loads(\"{}\", object_pairs_hook=collections.OrderedDict)\n");
        sb.append("output['B']['b1'] = \"subdatabefore\"\n");
        sb.append("output['B']['C'] = json.loads(\"{}\", object_pairs_hook=collections.OrderedDict)\n");
        sb.append("output['B']['C']['c1'] = \"subsubdatabefore\"\n");
        sb.append("output['B']['C']['c2'] = 33\n");
        sb.append("output['B']['C']['c3'] = 55l\n");
        sb.append("output['B']['b2'] = \"subdataend\"\n");
        properties.pythonCode.setValue(sb.toString());
        PythonRowDoFn function = new PythonRowDoFn();
        assertEquals(ValidationResult.OK, function.initialize(null, properties));
        DoFnTester<IndexedRecord, IndexedRecord> fnTester = DoFnTester.of(function);
        List<IndexedRecord> outputs = fnTester.processBundle(inputIndexedRecord);
        assertEquals(1, outputs.size());

        GenericRecord outputRecord = (GenericRecord) outputs.get(0);
        compareRecords(outputIndexedRecord, outputRecord);
    }

    @Test
    public void test_FlatMap_doNothing() throws Exception {

        PythonRowProperties properties = new PythonRowProperties("test");
        properties.init();
        properties.mapType.setValue(MapType.FLATMAP);
        properties.pythonCode.setValue("outputList.append(input)");
        PythonRowDoFn function = new PythonRowDoFn();
        assertEquals(ValidationResult.OK, function.initialize(null, properties));
        DoFnTester<IndexedRecord, IndexedRecord> fnTester = DoFnTester.of(function);
        List<IndexedRecord> outputs = fnTester.processBundle(inputIndexedRecord);
        assertEquals(1, outputs.size());

        GenericRecord outputRecord = (GenericRecord) outputs.get(0);
        compareRecords(inputIndexedRecord, outputRecord);
    }

    @Test
    public void test_FlatMap_DuplicateInput() throws Exception {

        PythonRowProperties properties = new PythonRowProperties("test");
        properties.init();
        properties.mapType.setValue(MapType.FLATMAP);
        StringBuilder sb = new StringBuilder();
        sb.append("import copy\n");
        sb.append("outputList.append(input)\n");
        sb.append("outputList.append(input)\n");
        sb.append("outputList.append(copy.deepcopy(input))\n");
        properties.pythonCode.setValue(sb.toString());
        PythonRowDoFn function = new PythonRowDoFn();
        assertEquals(ValidationResult.OK, function.initialize(null, properties));
        DoFnTester<IndexedRecord, IndexedRecord> fnTester = DoFnTester.of(function);
        List<IndexedRecord> outputs = fnTester.processBundle(inputIndexedRecord);
        assertEquals(3, outputs.size());

        for (int i = 0; i < 3; i++) {
            GenericRecord outputRecord = (GenericRecord) outputs.get(i);
            compareRecords(inputIndexedRecord, outputRecord);
        }
    }

    @Test
    public void test_FlatMap_MultipleInputs() throws Exception {

        PythonRowProperties properties = new PythonRowProperties("test");
        properties.init();
        properties.mapType.setValue(MapType.FLATMAP);
        StringBuilder sb = new StringBuilder();
        sb.append("import copy\n");
        sb.append("outputList.append(input)\n");
        sb.append("outputList.append(input)\n");
        sb.append("outputList.append(copy.deepcopy(input))\n");
        properties.pythonCode.setValue(sb.toString());
        PythonRowDoFn function = new PythonRowDoFn();
        assertEquals(ValidationResult.OK, function.initialize(null, properties));
        DoFnTester<IndexedRecord, IndexedRecord> fnTester = DoFnTester.of(function);
        List<IndexedRecord> outputs = fnTester.processBundle(inputIndexedRecord, inputIndexedRecord, inputIndexedRecord);
        assertEquals(9, outputs.size());

        for (int i = 0; i < 9; i++) {
            GenericRecord outputRecord = (GenericRecord) outputs.get(i);
            compareRecords(inputIndexedRecord, outputRecord);
        }
    }

    @Test
    public void test_FlatMap_DupplicateOutputAndApplyATransformation() throws Exception {

        PythonRowProperties properties = new PythonRowProperties("test");
        properties.init();
        properties.mapType.setValue(MapType.FLATMAP);

        StringBuilder sb = new StringBuilder();
        sb.append("import copy\n");
        sb.append("outputList.append(copy.deepcopy(input))\n");
        sb.append("outputList.append(input)\n"); // will be converted to inputIndexedRecord2
        sb.append("output = input\n");
        sb.append("output['a1'] = \"rootdata2\"\n");
        sb.append("output['B']['b1'] = \"subdatabefore\"\n");
        sb.append("output['B']['C']['c1'] = \"subsubdatabefore\"\n");
        sb.append("output['B']['C']['c2'] = 33\n");
        sb.append("output['B']['C']['c3'] = 55l\n");
        sb.append("output['B']['b2'] = \"subdataend\"\n");
        sb.append("outputList.append(output)\n");
        properties.pythonCode.setValue(sb.toString());
        PythonRowDoFn function = new PythonRowDoFn();
        assertEquals(ValidationResult.OK, function.initialize(null, properties));
        DoFnTester<IndexedRecord, IndexedRecord> fnTester = DoFnTester.of(function);
        List<IndexedRecord> outputs = fnTester.processBundle(inputIndexedRecord);
        assertEquals(3, outputs.size());

        GenericRecord outputRecord1 = (GenericRecord) outputs.get(0);
        GenericRecord outputRecord2 = (GenericRecord) outputs.get(1);
        GenericRecord outputRecord3 = (GenericRecord) outputs.get(2);

        compareRecords(inputIndexedRecord, outputRecord1);
        compareRecords(outputIndexedRecord, outputRecord2);
        compareRecords(outputIndexedRecord, outputRecord3);
    }

    @Test
    public void test_utf8() throws Exception {
        PythonRowProperties properties = new PythonRowProperties("test");
        properties.init();
        properties.mapType.setValue(MapType.MAP);
        properties.pythonCode.setValue("output['a1'] = input['a1']");
        PythonRowDoFn function = new PythonRowDoFn();
        assertEquals(ValidationResult.OK, function.initialize(null, properties));
        DoFnTester<IndexedRecord, IndexedRecord> fnTester = DoFnTester.of(function);
        List<IndexedRecord> outputs = fnTester.processBundle(GenericDataRecordHelper.createRecord(new Object[] { utf8Sample }));
        assertEquals(utf8Sample, outputs.get(0).get(0));
    }

    /**
     * Compare Avro record field values.
     */
    public void compareRecords(final IndexedRecord expectedRecord, final GenericRecord outputRecord) {
        // a1
        assertEquals(expectedRecord.get(0).toString(), outputRecord.get(0).toString());

        // B
        GenericRecord outputRecordB = (GenericRecord) outputRecord.get(1);
        GenericRecord expectedRecordB = (GenericRecord) expectedRecord.get(1);
        // B.b1
        assertEquals(expectedRecordB.get("b1").toString(), outputRecordB.get(0).toString());
        // B.b2
        assertEquals(expectedRecordB.get("b2").toString(), outputRecordB.get(2).toString());

        // C
        GenericRecord outputRecordC = (GenericRecord) outputRecordB.get(1);
        GenericRecord expectedRecordC = (GenericRecord) expectedRecordB.get(1);
        assertEquals(expectedRecordC.toString(), outputRecordC.toString());
    }
}
