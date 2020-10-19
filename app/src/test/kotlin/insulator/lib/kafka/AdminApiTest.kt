package insulator.lib.kafka

import insulator.lib.kafka.model.Topic
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.StringSpec
import io.mockk.every
import io.mockk.mockk
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.Config
import org.apache.kafka.clients.admin.ConfigEntry
import org.apache.kafka.clients.admin.DescribeConfigsResult
import org.apache.kafka.clients.admin.DescribeTopicsResult
import org.apache.kafka.clients.admin.ListTopicsResult
import org.apache.kafka.clients.admin.TopicDescription
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.common.KafkaFuture
import org.apache.kafka.common.Node
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.TopicPartitionInfo
import org.apache.kafka.common.config.ConfigResource
import java.util.HashSet

class AdminApiTest : StringSpec({

    "listTopics happy path" {
        // arrange
        val listTopicResultMock = mockk<ListTopicsResult> {
            every { names() } returns KafkaFuture.completedFuture(HashSet<String>(1).also { it.add("Sample topic") })
        }
        val kafkaAdminClientMock = mockk<AdminClient> {
            every { listTopics() } returns listTopicResultMock
        }
        val sut = AdminApi(kafkaAdminClientMock, mockk())
        // act
        val res = sut.listTopics()
        // assert
        res shouldBeRight listOf("Sample topic")
    }

    "listTopics throws" {
        // arrange
        val exception = Throwable("Exception")
        val listTopicResultMock = mockk<ListTopicsResult> {
            every { names() } returns KafkaFuture.completedFuture(HashSet<String>()).thenApply { throw exception }
        }
        val kafkaAdminClientMock = mockk<AdminClient> {
            every { listTopics() } returns listTopicResultMock
        }
        val sut = AdminApi(kafkaAdminClientMock, mockk())
        // act
        val res = sut.listTopics()
        // assert
        res shouldBeLeft exception
    }

    "describeTopic happy path" {
        // arrange
        val partition = mockk<TopicPartitionInfo> {
            every { partition() } returns 0
            every { replicas() } returns listOf(Node(1, "", 1))
        }
        val describeTopicResultMock = mockk<DescribeTopicsResult> {
            every { all() } returns KafkaFuture.completedFuture(
                mapOf(
                    "topic2" to TopicDescription("topic2", false, listOf(partition, partition))
                )
            )
        }
        val resource = ConfigResource(ConfigResource.Type.TOPIC, "topic2")
        val describeConfigsMock = mockk<DescribeConfigsResult> {
            every { values() } returns mapOf(resource to KafkaFuture.completedFuture(Config(listOf(ConfigEntry("cleanup.policy", "compact")))))
        }
        val kafkaAdminClientMock = mockk<AdminClient> {
            every { describeTopics(any<Collection<String>>()) } returns describeTopicResultMock
            every { describeConfigs(any()) } returns describeConfigsMock
        }
        val consumerMock = mockk<Consumer<Any, Any>> {
            every { endOffsets(any()) } returns mapOf(TopicPartition("1", 1) to 5L)
            every { beginningOffsets(any()) } returns mapOf(TopicPartition("1", 1) to 0L)
        }
        val sut = AdminApi(kafkaAdminClientMock, consumerMock)
        // act
        val res = sut.describeTopic("topic2")
        // assert
        res shouldBeRight Topic("topic2", false, 2, 5, 1, true)
    }

    "Create topic happy path" {
        // arrange
        val kafkaAdminClientMock = mockk<AdminClient> {
            every { createTopics(any()) } returns mockk {
                every { all() } returns KafkaFuture.completedFuture(null)
            }
        }
        val consumerMock = mockk<Consumer<Any, Any>>()
        val sut = AdminApi(kafkaAdminClientMock, consumerMock)
        // act
        val res = sut.createTopics(Topic("name", null, 2, null, 1, false))
        // assert
        res shouldBeRight {}
    }

    "Create compacted topic happy path" {
        // arrange
        val kafkaAdminClientMock = mockk<AdminClient> {
            every { createTopics(any()) } returns mockk {
                every { all() } returns KafkaFuture.completedFuture(null)
            }
        }
        val consumerMock = mockk<Consumer<Any, Any>>()
        val sut = AdminApi(kafkaAdminClientMock, consumerMock)
        // act
        val res = sut.createTopics(Topic("name", null, 2, null, 1, true))
        // assert
        res shouldBeRight {}
    }
})
