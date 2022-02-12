package insulator.kafka

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.flatMap
import insulator.helper.runCatchingE
import insulator.kafka.model.Cluster
import insulator.kafka.model.Schema
import insulator.kafka.model.Subject
import io.confluent.kafka.schemaregistry.avro.AvroSchema
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient
import io.confluent.kafka.schemaregistry.client.rest.RestService

class SchemaRegistry(private val client: SchemaRegistryClient) {

    fun validate(schema: String) = runCatchingE { AvroSchema(schema).also { it.validate() } }

    fun register(subject: String, schema: String) = validate(schema)
        .flatMap { client.runCatchingE { register(subject, it) } }.map { Unit }

    fun deleteSubject(subject: String) =
        client.runCatchingE { deleteSubject(subject) }.map { Unit }

    fun deleteSchemaVersion(subject: String, version: Int) =
        client.runCatchingE { deleteSchemaVersion(subject, version.toString()) }.map { Unit }

    fun getAllSubjects(): Either<Throwable, Collection<String>> =
        client.runCatchingE { allSubjects.sorted() }

    suspend fun getSubject(subject: String): Either<Throwable, Subject> = either {
        val versions = client.runCatchingE { getAllVersions(subject) }.bind()
        Subject(
            subject,
            versions
                .map { getByVersion(subject, it).bind() }
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
