package insulator.lib.kafka

import arrow.fx.IO
import arrow.fx.extensions.fx
import insulator.lib.kafka.model.Topic
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

    fun listTopics(): IO<List<Topic>> = IO.fx {
        admin.listTopics().names().get().map { topicName -> Topic(topicName) }
    }

    fun describeTopic(vararg topicNames: String) = IO.fx {
        admin.describeTopics(topicNames.toList()).all().get().values
                .map {
                    Topic(
                            name = it.name(),
                            messageCount = null,
                            internal = it.isInternal,
                            partitions = it.partitions().size)

                }
    }

}

data class ClusterOverview(
        val brokerCount: Int,
        val topicsCount: Int,
        val consumerGroupsCount: Int
)