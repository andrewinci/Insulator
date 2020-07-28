package insulator.lib.kafka

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
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

    beforeTest {
        startKoin { modules(testConsumerFixture) }
    }

    test("start happy path") {
        // arrange
        val messages = mutableListOf<String>()
        val sut = Consumer()
        // act
        sut.start("testTopic", ConsumeFrom.Beginning, DeserializationFormat.String) { _, v, _ -> messages.add(v) }
        // assert
        delay(200)
        sut.stop()
        messages.size shouldBe 1
    }

    test("start happy path - now") {
        // arrange
        val messages = mutableListOf<String>()
        val sut = Consumer()
        // act
        sut.start("testTopic", ConsumeFrom.Now, DeserializationFormat.String) { _, v, _ -> messages.add(v) }
        // assert
        delay(200)
        sut.stop()
        messages.size shouldBe 0
    }

    test("isRunning") {
        // arrange
        val messages = mutableListOf<String>()
        val sut = Consumer()
        // act
        sut.start("testTopic", ConsumeFrom.Now, DeserializationFormat.String) { _, v, _ -> messages.add(v) }
        // assert
        sut.isRunning() shouldBe true
        sut.stop()
        sut.isRunning() shouldBe false
    }

    test("stop if not running") {
        // arrange
        val sut = Consumer()
        // act/assert
        sut.stop()
    }

    afterTest { stopKoin() }
})

val testConsumerFixture = module {
    scope(named("clusterScope")) {
        // Consumers
        scoped<org.apache.kafka.clients.consumer.Consumer<Any, Any>>() {
            MockConsumer<Any, Any>(OffsetResetStrategy.EARLIEST).also {
                val topicName = "testTopic"
                it.updatePartitions(topicName, listOf(PartitionInfo(topicName, 0, null, null, null)))
                it.updateBeginningOffsets(mapOf(TopicPartition(topicName, 0) to 0L))
                it.assign(listOf(TopicPartition(topicName, 0)))
                it.addRecord(ConsumerRecord(topicName, 0, 0L, "key", "value"))
            }
        }
    }
}
