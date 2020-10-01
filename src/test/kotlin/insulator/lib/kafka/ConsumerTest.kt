package insulator.lib.kafka

import arrow.core.left
import arrow.core.right
import insulator.lib.configuration.model.Cluster
import insulator.lib.jsonhelper.AvroToJsonConverter
import insulator.lib.jsonhelper.UnsupportedTypeException
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.delay
import org.apache.avro.Schema
import org.apache.avro.generic.GenericRecord
import org.apache.avro.generic.GenericRecordBuilder
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.MockConsumer
import org.apache.kafka.clients.consumer.OffsetResetStrategy
import org.apache.kafka.common.PartitionInfo
import org.apache.kafka.common.TopicPartition
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module

class ConsumerTest : FunSpec({
    val mockConverter = mockk<AvroToJsonConverter> { every { parse(any()) } answers { firstArg<GenericRecord>().toString().right() } }

    test("start happy path") {
        // arrange
        val messages = mutableListOf<String>()
        val sut = Consumer(Cluster.empty(), mockConverter)
        // act
        sut.start("testTopic", ConsumeFrom.Beginning, DeserializationFormat.String) { messages.addAll(it.map { record -> record.b }) }
        // assert
        delay(200)
        sut.stop()
        messages.size shouldBe 1
    }

    test("start happy path - avro consumer") {
        // arrange
        val messages = mutableListOf<String>()
        val sut = Consumer(Cluster.empty(), mockConverter)
        // act
        sut.start("testTopic", ConsumeFrom.Beginning, DeserializationFormat.Avro) { messages.addAll(it.map { record -> record.b }) }
        // assert
        delay(200)
        sut.stop()
        messages.size shouldBe 1
    }

    test("start happy path - unsupported schema for custom avro converter") {
        // arrange
        val mockInvalidSchemaConverter = mockk<AvroToJsonConverter> { every { parse(any()) } returns UnsupportedTypeException("").left() }
        val messages = mutableListOf<String>()
        val sut = Consumer(Cluster.empty(), mockInvalidSchemaConverter)
        // act
        sut.start("testTopic", ConsumeFrom.Beginning, DeserializationFormat.Avro) { messages.addAll(it.map { record -> record.b }) }
        // assert
        delay(200)
        sut.stop()
        messages.size shouldBe 1
    }

    test("start happy path - now") {
        // arrange
        val messages = mutableListOf<String>()
        val sut = Consumer(Cluster.empty(), mockConverter)
        // act
        sut.start("testTopic", ConsumeFrom.Now, DeserializationFormat.String) { messages.addAll(it.map { record -> record.b }) }
        // assert
        delay(200)
        sut.stop()
        messages.size shouldBe 0
    }

    test("isRunning") {
        // arrange
        val messages = mutableListOf<String>()
        val sut = Consumer(Cluster.empty(), mockConverter)
        // act
        sut.start("testTopic", ConsumeFrom.Now, DeserializationFormat.String) { messages.addAll(it.map { record -> record.b }) }
        // assert
        sut.isRunning() shouldBe true
        sut.stop()
        sut.isRunning() shouldBe false
    }

    test("stop if not running") {
        // arrange
        val sut = Consumer(Cluster.empty(), mockConverter)
        // act/assert
        sut.stop()
    }

    lateinit var fixture: TestConsumerFixture
    beforeTest {
        fixture = TestConsumerFixture()
        startKoin { modules(fixture.koinModule) }
    }

    afterTest {
        fixture.close()
    }
})

class TestConsumerFixture {

    val koinModule = module {
        scope<Cluster> {
            // Consumers
            scoped<org.apache.kafka.clients.consumer.Consumer<Any, Any>> { stringConsumer }
            scoped<org.apache.kafka.clients.consumer.Consumer<Any, Any>>(named("avroConsumer")) { avroConsumer }
        }
    }

    private val stringConsumer = MockConsumer<Any, Any>(OffsetResetStrategy.EARLIEST).also {
        val topicName = "testTopic"
        it.updatePartitions(topicName, listOf(PartitionInfo(topicName, 0, null, null, null)))
        it.updateBeginningOffsets(mapOf(TopicPartition(topicName, 0) to 0L))
        it.updateEndOffsets(mapOf(TopicPartition(topicName, 0) to 1L))
        it.assign(listOf(TopicPartition(topicName, 0)))
        it.addRecord(ConsumerRecord(topicName, 0, 0L, "key", "value"))
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

    fun close() {
        stringConsumer.runCatching { close() }
        avroConsumer.runCatching { close() }
        stopKoin()
    }
}
