package insulator.integrationtest.kafka

import insulator.kafka.adminApi
import insulator.kafka.factories.kafkaConfig
import insulator.kafka.model.Cluster
import insulator.kafka.model.ConsumerGroupState
import insulator.kafka.model.Topic
import insulator.kafka.model.TopicPartitionLag
import insulator.kafka.producer.stringProducer
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.assertions.timing.eventually
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.wait.strategy.Wait
import java.io.Closeable
import java.time.Duration
import java.util.Properties
import kotlin.concurrent.thread
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

@ExperimentalTime
class ConsumerGroupTest : FreeSpec({

    "test consumer group" - {
        // arrange
        val testTopicName = "test-topic"
        val consumerGroupName = "test-consumer-group"
        val cluster = KafkaContainer().also {
            it.start()
            it.waitingFor(Wait.forListeningPort())
        }
        val clusterConfig = Cluster(name = "Test cluster", endpoint = cluster.bootstrapServers)
        val adminApi = adminApi(clusterConfig)
        val producer = stringProducer(clusterConfig)
        val consumerConfig = kafkaConfig(clusterConfig).apply {
            put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupName)
            put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)
        }
        producer.send(testTopicName, "test", "test", null)

        "get list of consumer groups" {
            // start a consumer
            testConsumer(consumerConfig, testTopicName).use {
                eventually(10.seconds) {
                    // act
                    val consumerGroupList = adminApi.listConsumerGroups()

                    // assert
                    consumerGroupList shouldBeRight {
                        it shouldContain consumerGroupName
                    }
                }
            }
        }

        "describe empty consumer group" {
            testConsumer(consumerConfig, testTopicName).close()
            // act
            val consumerGroup = adminApi.describeConsumerGroup(consumerGroupName)
            // assert
            consumerGroup shouldBeRight {
                it.groupId shouldBe consumerGroupName
                it.state shouldBe ConsumerGroupState.EMPTY
                it.members.size shouldBe 0
            }
        }

        "describe running consumer group" {
            val anotherTopicName = "another-topic-name"
            adminApi.createTopics(Topic(name = anotherTopicName, replicationFactor = 1, partitionCount = 3))
            val consumers = (1..3).map { testConsumer(consumerConfig, anotherTopicName) }
            eventually(20.seconds) {
                // act
                val consumerGroup = adminApi.describeConsumerGroup(consumerGroupName)
                // assert
                consumerGroup shouldBeRight { group ->
                    group.groupId shouldBe consumerGroupName
                    group.state shouldBe ConsumerGroupState.STABLE
                    group.members.size shouldBe 3
                    group.members.forEach { it.clientId shouldStartWith ("consumer-$consumerGroupName") }
                    group.members.map { it.topicPartitions }.flatten() shouldContainAll (0..2).map { TopicPartitionLag(anotherTopicName, it, 0) }
                }
            }
            consumers.forEach { it.close() }
        }
    }
})

fun testConsumer(consumerConfig: Properties, topicName: String) = object : Closeable {

    private var isRunning = true

    private val consumerThread = with(KafkaConsumer<String, String>(consumerConfig)) {
        subscribe(listOf(topicName))
        thread(start = true) {
            while (isRunning) {
                poll(Duration.ofMillis(200))
                commitSync()
            }
            this.close()
        }
    }

    override fun close() {
        isRunning = false
        consumerThread.join()
    }
}
