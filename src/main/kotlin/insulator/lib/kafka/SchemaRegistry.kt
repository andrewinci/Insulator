package insulator.lib.kafka

import insulator.lib.kafka.model.Schema
import insulator.lib.kafka.model.Subject
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient

class SchemaRegistry(private val client: SchemaRegistryClient) {
    fun getAllSubjects(): Collection<String> = client.allSubjects.sorted()

    fun getSubject(subject: String): Subject {
        val versions = client.getAllVersions(subject)
                .map { client.getByVersion(subject, it, false) }
                .map { Schema(it.schema, it.version) }
        return Subject(subject, versions)
    }

}