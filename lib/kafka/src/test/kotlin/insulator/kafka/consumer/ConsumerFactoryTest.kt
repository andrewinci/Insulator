package insulator.kafka.consumer

import insulator.kafka.model.Cluster
import insulator.kafka.model.SchemaRegistryConfiguration
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

class ConsumerFactoryTest : StringSpec({

    "consumer factory cache" {
        // arrange
        val sut = ConsumerFactory(Cluster.empty().copy(endpoint = "localhost:8080"))
        // act
        val first = sut.build(DeserializationFormat.String)
        val second = sut.build(DeserializationFormat.String)
        // assert
        first shouldBeSameInstanceAs second
    }

    "consumer factory build avro happy path" {
        // arrange
        val schemaRegistry = SchemaRegistryConfiguration("localhost:8080")
        val sut = ConsumerFactory(
            Cluster.empty().copy(
                endpoint = "localhost:8080",
                schemaRegistryConfig = schemaRegistry
            )
        )
        // act
        val consumer = sut.build(DeserializationFormat.Avro)
        // assert
        consumer shouldNotBe null
    }
})
