package org.talend.components.adapter.beam.schemaregistry;

import org.apache.avro.Schema;

/**
 * Base interface for schema registry implementation
 */
public interface SchemaRegistry {

    public Schema get(String id);

    public void put(String id, Schema schema);
}
