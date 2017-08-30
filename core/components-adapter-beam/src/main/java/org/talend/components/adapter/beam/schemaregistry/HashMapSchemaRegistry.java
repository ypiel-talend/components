package org.talend.components.adapter.beam.schemaregistry;

import java.util.HashMap;

import org.apache.avro.Schema;

/**
 * Simple SchemaRegistryInterface implementation based on a local HashMap
 *
 */
public class HashMapSchemaRegistry implements SchemaRegistry {

    private final static HashMap<String, Schema> schemaRegistry = new HashMap<>();

    @Override
    public Schema get(String id) {
        return schemaRegistry.get(id);
    }

    @Override
    public void put(String id, Schema schema) {
        schemaRegistry.put(id, schema);
    }
}