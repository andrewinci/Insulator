package insulator.lib.kafka

import arrow.core.right
import insulator.lib.jsonhelper.JsonToAvroConverter
import insulator.lib.kafka.model.Schema
import insulator.lib.kafka.model.Subject
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.core.spec.style.FunSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.producer.Producer

class AvroProducerTest : FunSpec({

    test("avro producer caches schemas") {
        // arrange
        val testMessage = "test-message"
        val topic = "topic-name"
        val schemaRegistry = mockk<SchemaRegistry> {
            every { getSubject(any()) } returns Subject("$topic-value", listOf(Schema("", 1))).right()
        }
        val jsonAvroConverter = mockk<JsonToAvroConverter> {
            every { convert(any(), any()) } returns mockk<GenericRecord>().right()
        }
        val sut = AvroProducer(mockk(), schemaRegistry, jsonAvroConverter)
        // act
        repeat(5) { sut.validate(testMessage, topic) }
        repeat(5) { sut.send(topic, testMessage, "key") }
        // assert
        verify(exactly = 1) { schemaRegistry.getSubject(any()) }
    }

    test("send return an error if the underlying operation fails") {
        // arrange
        val error = Throwable("error message")
        val topic = "topic-name"
        val schemaRegistry = mockk<SchemaRegistry> {
            every { getSubject(any()) } returns Subject("$topic-value", listOf(Schema("", 1))).right()
        }
        val jsonAvroConverter = mockk<JsonToAvroConverter> {
            every { convert(any(), any()) } returns mockk<GenericRecord>().right()
        }
        val producer = mockk<Producer<String, GenericRecord>> {
            every { send(any()) } throws error
        }
        val sut = AvroProducer(producer, schemaRegistry, jsonAvroConverter)
        // act
        val res = sut.send(topic, "test message", "key")
        // assert
        res shouldBeLeft error
    }
})
