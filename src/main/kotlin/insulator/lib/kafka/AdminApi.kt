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

    fun listTopics(): IO<List<Topic>> = IO.fx {
        admin.listTopics().names().get().map { topicName -> Topic(topicName) }
    }

    fun describeTopic(vararg topicNames: String) = IO.fx {
        val topicDescriptions = admin.describeTopics(topicNames.toList()).all().get().values
        val recordCount = topicDescriptions
                .map { it.name() to it.toTopicPartitions() }
                .map { (name, partitions) -> name to consumer.endOffsets(partitions).values.sum() - consumer.beginningOffsets(partitions).values.sum() }
                .toMap()
        topicDescriptions.map {
            Topic(
                    name = it.name(),
                    messageCount = recordCount.getOrDefault(it.name(), null),
                    internal = it.isInternal,
                    partitions = it.partitions().size
            )
        }
    }

    private fun TopicDescription.toTopicPartitions() = this.partitions().map { TopicPartition(this.name(), it.partition()) }

}

data class ClusterOverview(
        val brokerCount: Int,
        val topicsCount: Int,
        val consumerGroupsCount: Int
)