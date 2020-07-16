package insulator.lib.kafka

import arrow.fx.IO
import arrow.fx.extensions.fx
import insulator.lib.kafka.model.Topic
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.TopicDescription
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.common.TopicPartition

class AdminApi(private val admin: AdminClient, private val consumer: Consumer<Any, Any>) {

    fun getOverview(): ClusterOverview {
        val nodes = admin.describeCluster().nodes()
        val topics = admin.listTopics()
        val consumers = admin.listConsumerGroups()
        return ClusterOverview(
                brokerCount = nodes.get().size,
                topicsCount = topics.names().get().size,
                consumerGroupsCount = consumers.all().get().size
        )
    }

    fun listTopics() = IO { admin.listTopics().names().get().toList() }

    fun describeTopic(vararg topicNames: String) = IO {
        val topicDescriptions = admin.describeTopics(topicNames.toList()).all().get().values
        topicDescriptions.map {
            Topic(
                    name = it.name(),
                    internal = it.isInternal,
                    partitions = it.partitions().size
            )
        }
    }

    fun getApproximateMessageCount(topicName: String) = IO {
        val topicDescription = admin.describeTopics(listOf(topicName)).all().get().values.first()
        val topicPartition = topicDescription.toTopicPartitions()
        consumer.endOffsets(topicPartition).values.sum() - consumer.beginningOffsets(topicPartition).values.sum()
    }

    private fun TopicDescription.toTopicPartitions() = this.partitions().map { TopicPartition(this.name(), it.partition()) }

}

data class ClusterOverview(
        val brokerCount: Int,
        val topicsCount: Int,
        val consumerGroupsCount: Int
)