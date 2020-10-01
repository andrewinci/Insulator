package insulator.lib.configuration.model

import insulator.lib.jsonhelper.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Cluster(
    @Serializable(with = UUIDSerializer::class)
    val guid: UUID,
    val name: String,
    val endpoint: String,

    val useSSL: Boolean = false,
    val sslConfiguration: SslConfiguration = SslConfiguration(),

    val useSasl: Boolean = false,
    val saslConfiguration: SaslConfiguration = SaslConfiguration(),

    val schemaRegistryConfig: SchemaRegistryConfiguration = SchemaRegistryConfiguration()
) {
    companion object {
        fun empty() = Cluster(UUID.randomUUID(), "", "")
    }

    fun isSchemaRegistryConfigured() = !schemaRegistryConfig?.endpoint.isNullOrEmpty()
}

@Serializable
data class SslConfiguration(
    val sslTruststoreLocation: String? = null,
    val sslTruststorePassword: String? = null,
    val sslKeystoreLocation: String? = null,
    val sslKeyStorePassword: String? = null
)

@Serializable
data class SaslConfiguration(
    val saslUsername: String? = null,
    val saslPassword: String? = null
)

@Serializable
data class SchemaRegistryConfiguration(
    val endpoint: String? = null,
    val username: String? = null,
    val password: String? = null
)
