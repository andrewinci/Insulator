package insulator.kafka.producer

import arrow.core.right
import insulator.kafka.SchemaRegistry
import insulator.kafka.model.Schema
import insulator.kafka.model.Subject
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.producer.Producer

class AvroProducerTest : StringSpec({

    "avro producer caches schemas" {
        // arrange
        val testMessage = "test-message"
        val topic = "topic-name"
        val schemaRegistry = mockk<SchemaRegistry> {
            coEvery { getSubject(any()) } returns Subject("$topic-value", listOf(Schema("", 1, 2))).right()
        }
        val sut = AvroProducer(mockk(), schemaRegistry) { _, _ -> mockk<GenericRecord>().right() }
        // act
        repeat(5) { sut.validate(testMessage, topic) }
        repeat(5) { sut.send(topic, testMessage, "key") }
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
        val sut = AvroProducer(producer, schemaRegistry) { _, _ -> mockk<GenericRecord>().right() }
        // act
        val res = sut.send(topic, "test message", "key")
        // assert
        res shouldBeLeft error
    }
})
