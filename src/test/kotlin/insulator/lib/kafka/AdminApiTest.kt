package insulator.lib.kafka

import insulator.lib.kafka.model.Topic
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.FunSpec
import io.mockk.every
import io.mockk.mockk
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.DescribeTopicsResult
import org.apache.kafka.clients.admin.ListTopicsResult
import org.apache.kafka.clients.admin.TopicDescription
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.common.KafkaFuture
import org.apache.kafka.common.Node
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.TopicPartitionInfo
import java.util.HashSet

class AdminApiTest : FunSpec({

    test("listTopics happy path") {
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
        res.get() shouldBeRight listOf("Sample topic")
    }

    test("listTopics throws") {
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
        res.get() shouldBeLeft exception
    }

    test("describeTopic happy path") {
        // arrange
        val partition = mockk<TopicPartitionInfo> {
            every { partition() } returns 0
            every { replicas() } returns listOf(Node(1, "", 1))
        }
        val describeTopicResultMock = mockk<DescribeTopicsResult> {
            every { all() } returns KafkaFuture.completedFuture(
                mapOf(
                    "topic1" to TopicDescription("topic1", true, listOf(partition)),
                    "topic2" to TopicDescription("topic2", false, listOf(partition, partition))
                )
            )
        }
        val kafkaAdminClientMock = mockk<AdminClient> {
            every { describeTopics(any<Collection<String>>()) } returns describeTopicResultMock
        }
        val consumerMock = mockk<Consumer<Any, Any>> {
            every { endOffsets(any()) } returns mapOf(TopicPartition("1", 1) to 5L)
            every { beginningOffsets(any()) } returns mapOf(TopicPartition("1", 1) to 0L)
        }
        val sut = AdminApi(kafkaAdminClientMock, consumerMock)
        // act
        val res = sut.describeTopic("topic1", "topic2")
        // assert
        res.get() shouldBeRight listOf(Topic("topic1", true, 1, 5, 1), Topic("topic2", false, 2, 5, 1))
    }

    test("Create topic happy path") {
        // arrange
        val kafkaAdminClientMock = mockk<AdminClient> {
            every { createTopics(any()) } returns mockk {
                every { all() } returns KafkaFuture.completedFuture(null)
            }
        }
        val consumerMock = mockk<Consumer<Any, Any>>()
        val sut = AdminApi(kafkaAdminClientMock, consumerMock)
        // act
        val res = sut.createTopics(Topic("name", null, 2, null, 1))
        // asssert
        res.get() shouldBeRight {}
    }
})
