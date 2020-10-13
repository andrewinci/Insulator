package insulator.lib.kafka

import arrow.core.Either
import arrow.core.extensions.fx
import arrow.core.flatMap
import insulator.lib.helpers.runCatchingE
import insulator.lib.kafka.model.Subject
import io.confluent.kafka.schemaregistry.avro.AvroSchema
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient
import org.apache.avro.Schema
import insulator.lib.kafka.model.Schema as InsulatorSchema

class SchemaRegistry(private val client: SchemaRegistryClient) {

    fun deleteSubject(subject: String) =
        client.runCatchingE { deleteSubject(subject) }.map { Unit }

    fun getAllSubjects(): Either<Throwable, Collection<String>> =
        client.runCatchingE { allSubjects.sorted() }

    fun getSubject(subject: String) =
        Either.fx<Throwable, Subject> {
            val versions = !client.runCatchingE { getAllVersions(subject) }
            Subject(
                subject,
                versions
                    .map { !getByVersion(subject, it) }
                    .map { InsulatorSchema(it.schema, it.version, it.id) }
            )
        }

    fun addSchema(subject: String, schema: String) =
        Schema.Parser().runCatchingE { parse(schema) }
            .map { AvroSchema(it) }
            .flatMap { parsedSchema -> client.runCatchingE { register(subject, parsedSchema) } }

    private fun getByVersion(subject: String, version: Int) =
        client.runCatchingE { getByVersion(subject, version, false) }
}
