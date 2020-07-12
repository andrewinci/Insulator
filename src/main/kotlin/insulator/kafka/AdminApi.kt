package insulator.kafka

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import arrow.fx.IO
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.concurrent.Promise
import insulator.model.Topic
import org.apache.kafka.clients.admin.AdminClient
import java.lang.Exception

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

}

data class ClusterOverview(
        val brokerCount: Int,
        val topicsCount: Int,
        val consumerGroupsCount: Int
)