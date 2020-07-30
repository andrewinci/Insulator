package insulator.lib.kafka.model

import org.apache.kafka.common.ConsumerGroupState

data class ConsumerGroup(val id: String, val state: ConsumerGroupState, val members: List<ConsumerGroupMember>)
data class ConsumerGroupMember(val clientId: String, val topicPartitions: List<TopicPartitionLag>)
data class TopicPartitionLag(val topic: String, val partition: Int, val lag: Long)
