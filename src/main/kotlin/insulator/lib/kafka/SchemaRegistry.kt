package insulator.lib.kafka

import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient

class SchemaRegistry(private val client: SchemaRegistryClient){
    fun getAllSubjects() : Collection<String> = client.allSubjects.sorted()
}