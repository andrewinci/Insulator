package insulator.kafka.factories

import insulator.kafka.model.Cluster
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.config.SaslConfigs
import org.apache.kafka.common.config.SslConfigs
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import java.util.Properties

fun kafkaConfig(cluster: Cluster) = Properties().apply {
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
        put(SaslConfigs.SASL_MECHANISM, if (cluster.saslConfiguration.useScram) "SCRAM-SHA-256" else "PLAIN")
        put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, SslConfigs.DEFAULT_SSL_ENDPOINT_IDENTIFICATION_ALGORITHM)
        put(
            SaslConfigs.SASL_JAAS_CONFIG,
            (if (cluster.saslConfiguration.useScram)
                "org.apache.kafka.common.security.scram.ScramLoginModule "
            else "org.apache.kafka.common.security.plain.PlainLoginModule ")
                + "required username=\"${cluster.saslConfiguration.saslUsername}\" password=\"${cluster.saslConfiguration.saslPassword}\";"
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
