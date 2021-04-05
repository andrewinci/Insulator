package insulator.kafka.local

import kotlinx.coroutines.withTimeout
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.Network

// taken from: https://github.com/gAmUssA/testcontainers-java-module-confluent-platform/blob/master/src/main/java/io/confluent/testcontainers/SchemaRegistryContainer.java
class SchemaRegistryContainer : GenericContainer<SchemaRegistryContainer?>("confluentinc/cp-schema-registry:5.4.3") {
    init {
        withExposedPorts(8081)
    }

    fun withKafka(kafka: KafkaContainer): SchemaRegistryContainer =
        withKafka(kafka.networkAliases[0].toString() + ":9093")

    private fun withKafka(bootstrapServers: String): SchemaRegistryContainer {
        withEnv("SCHEMA_REGISTRY_HOST_NAME", "schema-registry")
        withEnv("SCHEMA_REGISTRY_LISTENERS", "http://0.0.0.0:8081")
        withEnv("SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS", "PLAINTEXT://$bootstrapServers")
        return self()!!
    }

    val endpoint: String
        get() = "http://" + containerIpAddress + ":" + getMappedPort(8081)
}
