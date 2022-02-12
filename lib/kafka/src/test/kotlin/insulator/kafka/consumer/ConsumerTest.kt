package insulator.kafka.consumer

import arrow.core.left
import arrow.core.right
import io.kotest.assertions.timing.eventually
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.stringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.delay
import org.apache.avro.Schema
import org.apache.avro.generic.GenericRecordBuilder
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.MockConsumer
import org.apache.kafka.clients.consumer.OffsetAndTimestamp
import org.apache.kafka.clients.consumer.OffsetResetStrategy
import org.apache.kafka.common.PartitionInfo
import org.apache.kafka.common.TopicPartition
import java.io.Closeable
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class ConsumerTest : StringSpec({

    fun testScenario(consumerFrom: ConsumeFrom, deserializationFormat: DeserializationFormat, expectedMessages: Int = 1) = stringSpec {
        "test start consuming from $consumerFrom and format $deserializationFormat" {
            TestConsumerScenario().use {
                // arrange
                val messages = mutableListOf<String>()
                // act
                it.sut.start("testTopic", consumerFrom, deserializationFormat) { lst -> messages.addAll(lst.map { r -> r.value }) }
                // assert
                delay(300L)
                eventually(3.seconds) {
                    messages.size shouldBe expectedMessages
                }
            }
        }
    }

    include(testScenario(ConsumeFrom.Beginning, DeserializationFormat.String))
    include(testScenario(ConsumeFrom.Beginning, DeserializationFormat.Avro))
    include(testScenario(ConsumeFrom.LastHour, DeserializationFormat.String))
    include(testScenario(ConsumeFrom.LastDay, DeserializationFormat.String))
    include(testScenario(ConsumeFrom.LastWeek, DeserializationFormat.String))
    include(testScenario(ConsumeFrom.Now, DeserializationFormat.Avro, 0))

    "start happy path - unsupported schema for custom avro converter" {
        TestConsumerScenario().use {
            // arrange
            val messages = mutableListOf<String>()
            val sut = Consumer(it.consumerFactory) { Throwable("").left() }
            // act
            sut.start("testTopic", ConsumeFrom.Beginning, DeserializationFormat.Avro) { lst -> messages.addAll(lst.map { r -> r.value }) }
            // assert
            eventually(20.seconds) {
                messages.size shouldBe 1
            }
        }
    }

    "isRunning" {
        TestConsumerScenario().use {
            // arrange
            val messages = mutableListOf<String>()
            // act
            it.sut.start("testTopic", ConsumeFrom.Now, DeserializationFormat.String) { lst -> messages.addAll(lst.map { r -> r.value }) }
            // assert
            it.sut.isRunning() shouldBe true
            it.sut.stop()
            it.sut.isRunning() shouldBe false
        }
    }

    "stop without start doesn't throw" {
        TestConsumerScenario().use {
            // arrange
            // act/assert
            it.sut.stop()
        }
    }

    "start twice throw an error" {
        TestConsumerScenario().use {
            // arrange
            it.sut.start("testTopic", ConsumeFrom.Now, DeserializationFormat.String) { }
            // act
            val action = suspend { it.sut.start("testTopic", ConsumeFrom.Now, DeserializationFormat.String) { } }
            // assert
            kotlin.runCatching { action.invoke() }.isFailure shouldBe true
        }
    }
})

class TestConsumerScenario : Closeable {
    val consumerFactory = mockk<ConsumerFactory>() {
        every { build(any()) } answers {
            when (firstArg<DeserializationFormat>()) {
                DeserializationFormat.String -> stringConsumer
                DeserializationFormat.Avro -> avroConsumer
            }
        }
    }
    val sut = Consumer(consumerFactory) { it.toString().right() }

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
