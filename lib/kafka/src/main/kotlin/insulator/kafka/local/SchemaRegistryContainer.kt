package insulator.kafka.local

import org.testcontainers.containers.GenericContainer

// taken from: https://github.com/gAmUssA/testcontainers-java-module-confluent-platform/blob/master/src/main/java/io/confluent/testcontainers/SchemaRegistryContainer.java
class SchemaRegistryContainer(imageName: String) : GenericContainer<SchemaRegistryContainer?>(imageName) {
    init {
        withExposedPorts(8081)
    }

    fun withKafka(bootstrapServers: String): SchemaRegistryContainer {
        withEnv("SCHEMA_REGISTRY_HOST_NAME", "schema-registry")
        withEnv("SCHEMA_REGISTRY_LISTENERS", "http://0.0.0.0:8081")
        withEnv("SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS", bootstrapServers)
        return self()!!
    }

    val endpoint: String
        get() = "http://" + containerIpAddress + ":" + getMappedPort(8081)
}
