package insulator.kafka.factories

import insulator.kafka.model.Cluster
import insulator.kafka.model.SaslConfiguration
import insulator.kafka.model.SchemaRegistryConfiguration
import insulator.kafka.model.SslConfiguration
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class KafkaConfigKtTest : StringSpec({

    "plain test configuration" {
        // arrange
        val testEndpoint = "testEndpoint"
        // act
        val config = kafkaConfig(Cluster.empty().copy(endpoint = testEndpoint))
        // assert
        with(config) {
            get("bootstrap.servers") shouldBe testEndpoint
            get("auto.register.schemas") shouldBe false
            get("key.deserializer") shouldNotBe null
            get("key.serializer") shouldNotBe null
        }
    }

    "test schema registry configuration" {
        // arrange
        val testSchemaRegistryConfig = SchemaRegistryConfiguration(
            "endpoint",
            "username",
            "password"
        )
        // act
        val config = kafkaConfig(Cluster.empty().copy(schemaRegistryConfig = testSchemaRegistryConfig))
        // assert
        with(config) {
            get("schema.registry.url") shouldBe "endpoint"
            get("basic.auth.credentials.source") shouldBe "USER_INFO"
            get("basic.auth.user.info") shouldBe "username:password"
        }
    }

    "test ssl configuration" {
        // arrange
        val testSSLConfig = SslConfiguration(
            sslTruststoreLocation = "sslTruststoreLocation",
            sslTruststorePassword = "sslTruststorePassword",
            sslKeystoreLocation = "sslKeystoreLocation",
            sslKeyStorePassword = "sslKeystorePassword",
        )
        // act
        val config = kafkaConfig(Cluster.empty().copy(useSSL = true, sslConfiguration = testSSLConfig))
        // assert
        with(config) {
            get("security.protocol") shouldBe "SSL"
            get("ssl.keystore.type") shouldBe "PKCS12"
            get("ssl.truststore.location") shouldBe "sslTruststoreLocation"
            get("ssl.truststore.password") shouldBe "sslTruststorePassword"
            get("ssl.keystore.location") shouldBe "sslKeystoreLocation"
            get("ssl.keystore.password") shouldBe "sslKeystorePassword"
        }
    }

    "test sasl configuration" {
        // arrange
        val testSaslConfiguration = SaslConfiguration(
            saslUsername = "saslUsername",
            saslPassword = "saslPassword",
        )
        // act
        val config = kafkaConfig(Cluster.empty().copy(useSasl = true, saslConfiguration = testSaslConfiguration))
        // assert
        with(config) {
            get("security.protocol") shouldBe "SASL_SSL"
            get("sasl.mechanism") shouldBe "PLAIN"
            get("ssl.endpoint.identification.algorithm") shouldBe "https"
            get("sasl.jaas.config") shouldBe "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"saslUsername\"   password=\"saslPassword\";"
        }
    }
})
