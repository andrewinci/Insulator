package insulator.lib.kafka

import arrow.core.left
import arrow.core.right
import insulator.lib.configuration.model.Cluster
import insulator.lib.jsonhelper.avrotojson.AvroToJsonConverter
import insulator.lib.jsonhelper.avrotojson.UnsupportedTypeException
import insulator.lib.kafka.helpers.ConsumerFactory
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.delay
import org.apache.avro.Schema
import org.apache.avro.generic.GenericRecord
import org.apache.avro.generic.GenericRecordBuilder
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.MockConsumer
import org.apache.kafka.clients.consumer.OffsetAndTimestamp
import org.apache.kafka.clients.consumer.OffsetResetStrategy
import org.apache.kafka.common.PartitionInfo
import org.apache.kafka.common.TopicPartition
import java.io.Closeable

class ConsumerTest : StringSpec({

    "start happy path" {
        TestConsumerFixture().use {
            // arrange
            val messages = mutableListOf<String>()
            // act
            it.sut.start("testTopic", ConsumeFrom.Beginning, DeserializationFormat.String) { messages.addAll(it.map { record -> record.b }) }
            // assert
            delay(200)
            it.sut.stop()
            messages.size shouldBe 1
        }
    }

    "start happy path - avro consumer" {
        TestConsumerFixture().use {
            // arrange
            val messages = mutableListOf<String>()
            // act
            it.sut.start("testTopic", ConsumeFrom.Beginning, DeserializationFormat.Avro) { messages.addAll(it.map { record -> record.b }) }
            // assert
            delay(200)
            it.sut.stop()
            messages.size shouldBe 1
        }
    }

    "start happy path - last hour" {
        TestConsumerFixture().use {
            // arrange
            val messages = mutableListOf<String>()
            // act
            it.sut.start("testTopic", ConsumeFrom.LastHour, DeserializationFormat.String) { messages.addAll(it.map { record -> record.b }) }
            // assert
            delay(200)
            it.sut.stop()
            messages.size shouldBe 1
        }
    }

    "start happy path - last week" {
        TestConsumerFixture().use {
            // arrange
            val messages = mutableListOf<String>()
            // act
            it.sut.start("testTopic", ConsumeFrom.LastWeek, DeserializationFormat.String) { messages.addAll(it.map { record -> record.b }) }
            // assert
            delay(200)
            it.sut.stop()
            messages.size shouldBe 1
        }
    }

    "start happy path - unsupported schema for custom avro converter" {
        TestConsumerFixture().use {
            // arrange
            val mockInvalidSchemaConverter = mockk<AvroToJsonConverter> { every { parse(any()) } returns UnsupportedTypeException("").left() }
            val messages = mutableListOf<String>()
            val sut = Consumer(mockInvalidSchemaConverter, it.consumerFactory)
            // act
            sut.start("testTopic", ConsumeFrom.Beginning, DeserializationFormat.Avro) { messages.addAll(it.map { record -> record.b }) }
            // assert
            delay(200)
            sut.stop()
            messages.size shouldBe 1
        }
    }

    "start happy path - now" {
        TestConsumerFixture().use {
            // arrange
            val messages = mutableListOf<String>()
            // act
            it.sut.start("testTopic", ConsumeFrom.Now, DeserializationFormat.String) { messages.addAll(it.map { record -> record.b }) }
            // assert
            delay(200)
            it.sut.stop()
            messages.size shouldBe 0
        }
    }

    "isRunning" {
        TestConsumerFixture().use {
            // arrange
            val messages = mutableListOf<String>()
            // act
            it.sut.start("testTopic", ConsumeFrom.Now, DeserializationFormat.String) { messages.addAll(it.map { record -> record.b }) }
            // assert
            it.sut.isRunning() shouldBe true
            it.sut.stop()
            it.sut.isRunning() shouldBe false
        }
    }

    "stop without start doesn't throw" {
        TestConsumerFixture().use {
            // arrange
            // act/assert
            it.sut.stop()
        }
    }
})

class TestConsumerFixture : Closeable {
    val mockConverter = mockk<AvroToJsonConverter> { every { parse(any()) } answers { firstArg<GenericRecord>().toString().right() } }
    val consumerFactory = mockk<ConsumerFactory>() {
        every { build(DeserializationFormat.Avro) } returns avroConsumer
        every { build(DeserializationFormat.String) } returns stringConsumer
    }
    val sut = Consumer(mockConverter, consumerFactory)

    private val stringConsumer = object : MockConsumer<Any, Any>(OffsetResetStrategy.EARLIEST) {
        val topicName = "testTopic"

        init {
            updatePartitions(topicName, listOf(PartitionInfo(topicName, 0, null, null, null)))
            updateBeginningOffsets(mapOf(TopicPartition(topicName, 0) to 0L))
            updateEndOffsets(mapOf(TopicPartition(topicName, 0) to 1L))
            assign(listOf(TopicPartition(topicName, 0)))
            addRecord(ConsumerRecord(topicName, 0, 0L, "key", "value"))
        }

        override fun offsetsForTimes(timestampsToSearch: MutableMap<TopicPartition, Long>): Map<TopicPartition, OffsetAndTimestamp> {
            return timestampsToSearch.map { (tp, _) -> tp to OffsetAndTimestamp(0, 0) }.toMap()
        }
    }

    private val avroConsumer = MockConsumer<Any, Any>(OffsetResetStrategy.EARLIEST).also {
        val topicName = "testTopic"
        val schema = Schema.Parser().parse(
            """{ "type": "record", "name": "Sample", "fields": [{"name": "testField", "type": "string", "default": "test string"}]}"""
        )
        it.updatePartitions(topicName, listOf(PartitionInfo(topicName, 0, null, null, null)))
        it.updateBeginningOffsets(mapOf(TopicPartition(topicName, 0) to 0L))
        it.updateEndOffsets(mapOf(TopicPartition(topicName, 0) to 1L))
        it.assign(listOf(TopicPartition(topicName, 0)))
        it.addRecord(ConsumerRecord(topicName, 0, 0L, "key", GenericRecordBuilder(schema).build()))
    }

    override fun close() {
        stringConsumer.runCatching { close() }
        avroConsumer.runCatching { close() }
    }
}
