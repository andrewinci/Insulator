package insulator.kafka

import arrow.core.Either
import arrow.core.computations.either
import insulator.helper.runCatchingE
import insulator.kafka.model.Cluster
import insulator.kafka.model.Schema
import insulator.kafka.model.Subject
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient
import io.confluent.kafka.schemaregistry.client.rest.RestService

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

fun schemaRegistry(cluster: Cluster): SchemaRegistry {
    val config = cluster.schemaRegistryConfig
    val restService = RestService(config.endpoint)
    val credentials = mapOf(
        "basic.auth.credentials.source" to "USER_INFO",
        "basic.auth.user.info" to "${config.username}:${config.password}"
    )
    return SchemaRegistry(CachedSchemaRegistryClient(restService, 1000, credentials))
}
