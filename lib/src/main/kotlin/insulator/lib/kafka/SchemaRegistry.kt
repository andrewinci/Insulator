package insulator.lib.kafka

import arrow.core.Either
import arrow.core.computations.either
import insulator.lib.helpers.runCatchingE
import insulator.lib.kafka.model.Schema
import insulator.lib.kafka.model.Subject
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient

class SchemaRegistry(private val client: SchemaRegistryClient) {

    fun deleteSubject(subject: String) =
        client.runCatchingE { deleteSubject(subject) }.map { Unit }

    fun getAllSubjects(): Either<Throwable, Collection<String>> =
        client.runCatchingE { allSubjects.sorted() }

    suspend fun getSubject(subject: String): Either<Throwable, Subject> = either {
        val versions = !client.runCatchingE { getAllVersions(subject) }
        Subject(
            subject,
            versions
                .map { !getByVersion(subject, it) }
                .map { Schema(it.schema, it.version, it.id) }
        )
    }

    private fun getByVersion(subject: String, version: Int) =
        client.runCatchingE { getByVersion(subject, version, false) }
}
