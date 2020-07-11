package insulator.kafka

import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.common.config.ConfigResource

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
}

data class ClusterOverview(
        val brokerCount: Int,
        val topicsCount: Int,
        val consumerGroupsCount: Int
)