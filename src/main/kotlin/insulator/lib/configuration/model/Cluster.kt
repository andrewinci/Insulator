package insulator.lib.configuration.model

import java.util.*

data class Cluster(
        val guid: UUID,
        val name: String,
        val endpoint: String,

        val useSSL: Boolean = false,
        val sslConfiguration: SslConfiguration? = null,

        val useSasl: Boolean = false,
        val saslConfiguration: SaslConfiguration? = null,

        val schemaRegistryConfig: SchemaRegistryConfiguration? = null
) {
    companion object { val Empty = Cluster(UUID.randomUUID(), "", "")}
    fun isSchemaRegistryConfigured() = schemaRegistryConfig?.endpoint != null
}

data class SslConfiguration(
        val sslTruststoreLocation: String? = null,
        val sslTruststorePassword: String? = null,
        val sslKeystoreLocation: String? = null,
        val sslKeyStorePassword: String? = null
)

data class SaslConfiguration(
        val saslUsername: String? = null,
        val saslPassword: String? = null
)

data class SchemaRegistryConfiguration(
        val endpoint: String? = null,
        val username: String? = null,
        val password: String? = null
)