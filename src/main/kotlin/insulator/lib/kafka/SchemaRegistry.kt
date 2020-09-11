package insulator.lib.kafka

import arrow.core.Either
import arrow.core.extensions.fx
import arrow.core.left
import arrow.core.right
import insulator.lib.helpers.toEither
import insulator.lib.kafka.model.Schema
import insulator.lib.kafka.model.Subject
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient

class SchemaRegistry(private val client: SchemaRegistryClient) {

    fun deleteSubject(subject: String) =
        client.runCatching { deleteSubject(subject) }
            .fold({ Unit.right() }, { it.left() })

    fun getAllSubjects(): Either<Throwable, Collection<String>> =
        client.runCatching { allSubjects.sorted() }.toEither()

    fun getSubject(subject: String) =
        Either.fx<Throwable, Subject> {
            val versions = !client.runCatching { getAllVersions(subject) }.toEither()
            Subject(
                subject,
                versions
                    .map { !getByVersion(subject, it) }
                    .map { Schema(it.schema, it.version) }
            )
        }

    private fun getByVersion(subject: String, version: Int) =
        client.runCatching { getByVersion(subject, version, false) }.toEither()
}
