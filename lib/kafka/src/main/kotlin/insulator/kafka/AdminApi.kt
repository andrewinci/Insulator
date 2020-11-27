package insulator.kafka

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.flatMap
import arrow.core.rightIfNotNull
import insulator.kafka.factories.kafkaConfig
import insulator.kafka.helper.toSuspendCoroutine
import insulator.kafka.model.Cluster
import insulator.kafka.model.ConsumerGroup
import insulator.kafka.model.ConsumerGroupMember
import insulator.kafka.model.Topic
import insulator.kafka.model.TopicConfiguration
import insulator.kafka.model.TopicPartitionLag
import insulator.kafka.model.toInternal
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.admin.TopicDescription
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.consumer.OffsetAndMetadata
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.config.ConfigResource
import org.apache.kafka.common.config.TopicConfig
import org.apache.kafka.common.serialization.StringDeserializer
import java.io.Closeable

class AdminApi(private val admin: AdminClient, private val consumer: Consumer<Any, Any>) : Closeable {

    suspend fun describeConsumerGroup(groupId: String) = either<Throwable, ConsumerGroup> {
        val getLag = { partition: TopicPartition, currentOffset: OffsetAndMetadata? ->
            consumer.endOffsets(mutableListOf(partition)).values.first() - (currentOffset?.offset() ?: 0)
        }
        val partitionToOffset = !admin.listConsumerGroupOffsets(groupId).partitionsToOffsetAndMetadata().toSuspendCoroutine()
        val description = !admin.describeConsumerGroups(listOf(groupId)).all().toSuspendCoroutine().map { it.values.first() }
        ConsumerGroup(
            groupId = description.groupId(),
            state = description.state().toInternal(),
            members = description.members().map {
                ConsumerGroupMember(
                    it.clientId(),
                    it.assignment().topicPartitions().map { tp -> TopicPartitionLag(tp.topic(), tp.partition(), getLag(tp, partitionToOffset[tp])) }
                )
            }
        )
    }

    suspend fun listConsumerGroups() = admin.listConsumerGroups().all().toSuspendCoroutine()
        .map { consumerGroup -> consumerGroup.map { it.groupId() } }

    suspend fun deleteConsumerGroup(consumerGroupId: String) = admin.deleteConsumerGroups(listOf(consumerGroupId))
        .all().toSuspendCoroutine().fold({ it }, { Unit })

    suspend fun listTopics() = admin.listTopics().names().toSuspendCoroutine().map { it.toList() }

    suspend fun describeTopic(topicName: String): Either<Throwable, Topic> = either {
        val configResource = ConfigResource(ConfigResource.Type.TOPIC, topicName)
        val configuration = !admin.describeConfigs(listOf(configResource)).values()[configResource]!!.toSuspendCoroutine()
            .map {
                TopicConfiguration(rawConfiguration = it.entries().map { config -> config.name() to config.value() }.toMap())
            }

        val description = !(
            admin.describeTopics(listOf(topicName)).all()
                .toSuspendCoroutine()
                .flatMap { it[topicName].rightIfNotNull { Throwable("Invalid response from KafkaAdmin describeTopics") } }
            )

        Topic(
            name = description.name(),
            isInternal = description.isInternal,
            partitionCount = description.partitions().size,
            messageCount = consumer.endOffsets(description.toTopicPartitions()).values.sum() - consumer.beginningOffsets(description.toTopicPartitions()).values.sum(),
            replicationFactor = description.partitions()[0].replicas().count().toShort(),
            isCompacted = configuration.isCompacted,
            configuration = configuration,
        )
    }

    suspend fun createTopics(vararg topics: Topic) =
        admin.createTopics(
            topics.map {
                NewTopic(it.name, it.partitionCount, it.replicationFactor)
                    .configs(
                        mapOf(TopicConfig.CLEANUP_POLICY_CONFIG to compactedConfig(it.isCompacted))
                            .plus(it.configuration.rawConfiguration)
                    )
            }
        ).all().thenApply { Unit }.toSuspendCoroutine()

    suspend fun deleteTopic(topicName: String) =
        admin.deleteTopics(listOf(topicName)).all().toSuspendCoroutine()

    private fun TopicDescription.toTopicPartitions() = this.partitions().map { TopicPartition(this.name(), it.partition()) }

    private fun compactedConfig(isCompacted: Boolean): String =
        if (isCompacted) TopicConfig.CLEANUP_POLICY_COMPACT
        else TopicConfig.CLEANUP_POLICY_DELETE

    override fun close() = admin.close()
}

fun adminApi(cluster: Cluster) =
    kafkaConfig(cluster)
        .apply { put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java) }
        .let { AdminApi(AdminClient.create(it), KafkaConsumer(it)) }
