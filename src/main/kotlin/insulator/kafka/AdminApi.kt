package insulator.kafka

import arrow.core.left
import arrow.core.right
import insulator.model.Topic
import org.apache.kafka.clients.admin.AdminClient

class AdminApi(private val admin: AdminClient) {

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

    fun listTopics() = admin.runCatching { listTopics().names().get() }
            .fold({ it.toList().map { topicName -> Topic(topicName) }.right() }, { it.left() })

}

data class ClusterOverview(
        val brokerCount: Int,
        val topicsCount: Int,
        val consumerGroupsCount: Int
)