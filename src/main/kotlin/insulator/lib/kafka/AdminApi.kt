package insulator.lib.kafka

import insulator.lib.helpers.map
import insulator.lib.helpers.toCompletableFuture
import insulator.lib.kafka.model.Topic
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.admin.TopicDescription
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.common.TopicPartition

class AdminApi(private val admin: AdminClient, private val consumer: Consumer<Any, Any>) {

    fun listTopics() = admin.listTopics().names().toCompletableFuture().map { it.toList() }

    fun describeTopic(vararg topicNames: String) =
        admin.describeTopics(topicNames.toList()).all().toCompletableFuture()
            .map { description ->
                val recordCount = description.values
                    .map { it.name() to it.toTopicPartitions() }
                    .map { (name, partitions) -> name to consumer.endOffsets(partitions).values.sum() - consumer.beginningOffsets(partitions).values.sum() }
                    .toMap()
                description.values.map {
                    Topic(
                        name = it.name(),
                        isInternal = it.isInternal,
                        partitionCount = it.partitions().size,
                        messageCount = recordCount.getOrDefault(it.name(), null),
                        replicationFactor = it.partitions()[0].replicas().count().toShort()
                    )
                }
            }

    fun createTopics(vararg topics: Topic) =
        admin.createTopics(
            topics.map {
                NewTopic(it.name, it.partitionCount, it.replicationFactor)
            }
        ).all().toCompletableFuture()

    private fun TopicDescription.toTopicPartitions() = this.partitions().map { TopicPartition(this.name(), it.partition()) }
}
