package insulator.di

import insulator.lib.configuration.model.Cluster
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient
import io.confluent.kafka.schemaregistry.client.rest.RestService
import io.confluent.kafka.serializers.KafkaAvroDeserializer
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
import java.util.Properties

val kafkaModule = module {

    factory { currentCluster }

    scope<Cluster> {
        // Kafka
        scoped { AdminClient.create(get<Properties>()) }

        scoped<SchemaRegistryClient> {
            val config = get<Cluster>().schemaRegistryConfig
            val restService = RestService(config?.endpoint)
            val credentials = mapOf(
                "basic.auth.credentials.source" to "USER_INFO",
                "basic.auth.user.info" to "${config?.username}:${config?.password}"
            )
            CachedSchemaRegistryClient(restService, 1000, credentials)
        }

        // Consumers
        factory<Consumer<Any, Any>> {
            val properties = get<Properties>()
            properties[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
            KafkaConsumer<Any, Any>(properties)
        }
        factory<Consumer<Any, Any>>(named("avroConsumer")) {
            val properties = get<Properties>()
            properties[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = KafkaAvroDeserializer::class.java
            KafkaConsumer<Any, Any>(properties)
        }

        // Properties
        scoped {
            val cluster = get<Cluster>()
            val properties = Properties().apply {
                put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, cluster.endpoint)
                put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000)
                put(AdminClientConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, 30000)
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
                    put(
                        SaslConfigs.SASL_JAAS_CONFIG,
                        "org.apache.kafka.common.security.plain.PlainLoginModule " +
                            "required username=\"${cluster.saslConfiguration?.saslUsername}\"   password=\"${cluster.saslConfiguration?.saslPassword}\";"
                    )
                }
                if (cluster.isSchemaRegistryConfigured()) {
                    with(cluster.schemaRegistryConfig!!) {
                        put("schema.registry.url", endpoint)
                        put("basic.auth.credentials.source", "USER_INFO")
                        put("basic.auth.user.info", "$username:$password")
                    }
                }
                put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)
            }
            properties
        }
    }
}
