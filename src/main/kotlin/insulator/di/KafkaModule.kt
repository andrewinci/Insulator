import insulator.di.GlobalConfiguration
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient
import io.confluent.kafka.schemaregistry.client.rest.RestService
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.config.SaslConfigs
import org.apache.kafka.common.config.SslConfigs
import org.apache.kafka.common.serialization.StringDeserializer
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.util.*

val kafkaModule = module {

    scope(named("clusterScope")) {
        // Kafka
        scoped { AdminClient.create(get<Properties>()) }
        scoped<Consumer<Any, Any>> { KafkaConsumer<Any, Any>(get<Properties>()) }

        scoped<SchemaRegistryClient> {
            val config = GlobalConfiguration.currentCluster.schemaRegistryConfig
            val restService = RestService(config?.endpoint)
            val credentials = mapOf(
                    "basic.auth.credentials.source" to "USER_INFO",
                    "basic.auth.user.info" to "${config?.username}:${config?.password}"
            )
            CachedSchemaRegistryClient(restService, 1000, credentials)
        }
    }

    factory {
        val cluster = GlobalConfiguration.currentCluster
        val properties = Properties().apply {
            put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, cluster.endpoint)
            if (cluster.useSSL) {
                put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, "PKCS12")
                put(AdminClientConfig.SECURITY_PROTOCOL_CONFIG, "SSL")
                put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, cluster.sslConfiguration?.sslTruststoreLocation)
                put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, cluster.sslConfiguration?.sslTruststorePassword)
                put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, cluster.sslConfiguration?.sslKeystoreLocation)
                put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, cluster.sslConfiguration?.sslKeyStorePassword)
            } else if (cluster.useSasl) {
                put(AdminClientConfig.SECURITY_PROTOCOL_CONFIG, "SASL_SSL")
                put(SaslConfigs.SASL_MECHANISM, "PLAIN")
                put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, SslConfigs.DEFAULT_SSL_ENDPOINT_IDENTIFICATION_ALGORITHM)
                put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule " +
                        "required username=\"${cluster.saslConfiguration?.saslUsername}\"   password=\"${cluster.saslConfiguration?.saslPassword}\";")
            }
            put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)
            put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)
        }
        properties
    }
}
