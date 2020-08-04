package insulator.lib.kafka

import arrow.core.Either
import arrow.core.rightIfNotNull
import insulator.lib.helpers.flatMap
import insulator.lib.helpers.map
import insulator.lib.helpers.toCompletableFuture
import insulator.lib.kafka.model.Topic
import insulator.lib.kafka.model.TopicConfiguration
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.admin.TopicDescription
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.config.ConfigResource
import org.apache.kafka.common.config.TopicConfig
import java.util.concurrent.CompletableFuture

class AdminApi(private val admin: AdminClient, private val consumer: Consumer<Any, Any>) {

    fun listTopics() = admin.listTopics().names().toCompletableFuture().map { it.toList() }

    fun describeTopic(topicName: String): CompletableFuture<Either<Throwable, Topic>> {
        val configurationTopicResult = with(ConfigResource(ConfigResource.Type.TOPIC, topicName)) {
            admin.describeConfigs(listOf(this)).values()[this]!!.toCompletableFuture()
        }.map {
            TopicConfiguration(isCompacted = it.get(TopicConfig.CLEANUP_POLICY_CONFIG).value() == TopicConfig.CLEANUP_POLICY_COMPACT)
        }

        val describeTopicResult = admin.describeTopics(listOf(topicName)).all().toCompletableFuture()
            .flatMap { it[topicName].rightIfNotNull { Throwable("Invalid response from KafkaAdmin describeTopics") } }

        return configurationTopicResult.thenCompose { config ->
            describeTopicResult.flatMap { description -> config.map { description to it } }
        }.map { (description, configuration) ->
            Topic(
                name = description.name(),
                isInternal = description.isInternal,
                partitionCount = description.partitions().size,
                messageCount = consumer.endOffsets(description.toTopicPartitions()).values.sum() - consumer.beginningOffsets(description.toTopicPartitions()).values.sum(),
                replicationFactor = description.partitions()[0].replicas().count().toShort(),
                isCompacted = configuration.isCompacted
            )
        }
    }

    fun createTopics(vararg topics: Topic) =
        admin.createTopics(
            topics.map {
                NewTopic(it.name, it.partitionCount, it.replicationFactor)
                    .configs(mapOf(TopicConfig.CLEANUP_POLICY_CONFIG to compactedConfig(it.isCompacted)))
            }
        ).all().toCompletableFuture()

    private fun TopicDescription.toTopicPartitions() = this.partitions().map { TopicPartition(this.name(), it.partition()) }

    private fun compactedConfig(isCompacted: Boolean): String =
        if (isCompacted) TopicConfig.CLEANUP_POLICY_COMPACT
        else TopicConfig.CLEANUP_POLICY_DELETE
}
