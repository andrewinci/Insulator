import insulator.configuration.ConfigurationRepo
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.config.SslConfigs
import org.apache.kafka.common.serialization.StringDeserializer
import org.koin.dsl.module
import java.util.*

val kafkaModule = module {

    factory { AdminClient.create(get<Properties>()) }
    factory<Consumer<Any, Any>> { KafkaConsumer<Any, Any>(get<Properties>()) }

    factory {
        val cluster = ConfigurationRepo.currentCluster
        val properties = Properties().apply {
            put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, cluster.endpoint)
            if (cluster.useSSL) {
                put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, "PKCS12")
                put(AdminClientConfig.SECURITY_PROTOCOL_CONFIG, "SSL")
                put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, cluster.sslTruststoreLocation)
                put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, cluster.sslTruststorePassword)
                put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, cluster.sslKeystoreLocation)
                put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, cluster.sslKeyStorePassword)
            }
            put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)
            put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)
            put(ConsumerConfig.GROUP_ID_CONFIG, "replace")
        }
        properties
    }
}
