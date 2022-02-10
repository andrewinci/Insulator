package insulator.kafka.producer

import arrow.core.right
import insulator.kafka.SchemaRegistry
import insulator.kafka.model.Cluster
import insulator.kafka.model.Schema
import insulator.kafka.model.SchemaRegistryConfiguration
import insulator.kafka.model.Subject
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldNotBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.producer.Producer

class AvroProducerTest : StringSpec({

    "factory happy path" {
        // arrange
        val mockCluster = Cluster.empty().copy(
            endpoint = "localhost:9090",
            schemaRegistryConfig = SchemaRegistryConfiguration("localhost:9191")
        )
        // act
        val sut = avroProducer(mockCluster, mockk(), mockk())
        // assert
        sut shouldNotBe null
    }

    "avro producer caches schemas" {
        // arrange
        val testMessage = "test-message"
        val topic = "topic-name"
        val schemaRegistry = mockk<SchemaRegistry> {
            coEvery { getSubject(any()) } returns Subject("$topic-value", listOf(Schema("", 1, 2))).right()
        }
        val sut = AvroProducer({ mockk() }, schemaRegistry) { _, _ -> mockk<GenericRecord>().right() }
        // act
        repeat(5) { sut.validate(testMessage, topic, null) }
        repeat(5) { sut.send(topic, testMessage, "key", null) }
        // assert
        coVerify(exactly = 1) { schemaRegistry.getSubject(any()) }
    }

    "send return an error if the underlying operation fails" {
        // arrange
        val error = Throwable("error message")
        val topic = "topic-name"
        val schemaRegistry = mockk<SchemaRegistry> {
            coEvery { getSubject(any()) } returns Subject("$topic-value", listOf(Schema("", 1, 2))).right()
        }
        val producer = mockk<Producer<String, GenericRecord>> {
            coEvery { send(any()) } throws error
        }
        val sut = AvroProducer({ producer }, schemaRegistry) { _, _ -> mockk<GenericRecord>().right() }
        // act
        val res = sut.send(topic, "test message", "key", null)
        // assert
        res shouldBeLeft error
    }
})
