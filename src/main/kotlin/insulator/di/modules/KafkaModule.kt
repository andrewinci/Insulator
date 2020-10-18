package insulator.di.modules

import dagger.Module
import dagger.Provides
import insulator.di.ClusterScope
import insulator.lib.configuration.model.Cluster
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient
import io.confluent.kafka.schemaregistry.client.rest.RestService
import io.confluent.kafka.serializers.KafkaAvroDeserializer
import io.confluent.kafka.serializers.KafkaAvroSerializer
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.config.SaslConfigs
import org.apache.kafka.common.config.SslConfigs
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import java.util.Properties
import javax.inject.Named

@Module
class KafkaModule {

    @Provides
    @ClusterScope
    fun providesAdminClient(properties: Properties): AdminClient =
        AdminClient.create(properties)

    @Provides
    @ClusterScope
    fun providesSchemaRegistryClient(cluster: Cluster): SchemaRegistryClient {
        val config = cluster.schemaRegistryConfig
        val restService = RestService(config.endpoint)
        val credentials = mapOf(
            "basic.auth.credentials.source" to "USER_INFO",
            "basic.auth.user.info" to "${config.username}:${config.password}"
        )
        return CachedSchemaRegistryClient(restService, 1000, credentials)
    }

    // Producers
    @Provides
    @ClusterScope
    fun providesStringProducer(properties: Properties): Producer<String, String> {
        properties[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        return KafkaProducer(properties)
    }

    @Provides
    @ClusterScope
    fun providesAvroProducer(properties: Properties): Producer<String, GenericRecord> {
        properties[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = KafkaAvroSerializer::class.java
        return KafkaProducer(properties)
    }

    // Consumers
    @Provides
    @ClusterScope
    fun providesStringConsumer(properties: Properties): Consumer<Any, Any> {
        properties[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        return KafkaConsumer(properties)
    }

    @Provides
    @ClusterScope
    @Named("avroConsumer")
    fun providesAvroConsumer(properties: Properties): Consumer<Any, Any> {
        properties[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = KafkaAvroDeserializer::class.java
        return KafkaConsumer(properties)
    }

    @Provides
    @ClusterScope
    fun providedProperty(cluster: Cluster) = Properties().apply {
        put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, cluster.endpoint)
        put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000)
        put(AdminClientConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, 30000)
        if (cluster.useSSL) {
            put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, "PKCS12")
            put(AdminClientConfig.SECURITY_PROTOCOL_CONFIG, "SSL")
            put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, cluster.sslConfiguration.sslTruststoreLocation)
            put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, cluster.sslConfiguration.sslTruststorePassword)
            put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, cluster.sslConfiguration.sslKeystoreLocation)
            put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, cluster.sslConfiguration.sslKeyStorePassword)
        } else if (cluster.useSasl) {
            put(AdminClientConfig.SECURITY_PROTOCOL_CONFIG, "SASL_SSL")
            put(SaslConfigs.SASL_MECHANISM, "PLAIN")
            put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, SslConfigs.DEFAULT_SSL_ENDPOINT_IDENTIFICATION_ALGORITHM)
            put(
                SaslConfigs.SASL_JAAS_CONFIG,
                "org.apache.kafka.common.security.plain.PlainLoginModule " +
                    "required username=\"${cluster.saslConfiguration.saslUsername}\"   password=\"${cluster.saslConfiguration.saslPassword}\";"
            )
        }
        if (cluster.isSchemaRegistryConfigured()) {
            with(cluster.schemaRegistryConfig) {
                put("schema.registry.url", endpoint)
                put("basic.auth.credentials.source", "USER_INFO")
                put("basic.auth.user.info", "$username:$password")
            }
        }
        put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)
        put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
        put("auto.register.schemas", false)
    }
}
