package insulator.kafka.model

data class ConsumerGroup(
    val groupId: String,
    val state: ConsumerGroupState,
    val members: List<ConsumerGroupMember>,
)

data class ConsumerGroupMember(
    val clientId: String,
    val topicPartitions: List<TopicPartitionLag>
)

data class TopicPartitionLag(
    val topic: String,
    val partition: Int,
    val lag: Long
)

enum class ConsumerGroupState {
    UNKNOWN,
    PREPARING_REBALANCE,
    COMPLETING_REBALANCE,
    STABLE,
    DEAD,
    EMPTY,
}

fun org.apache.kafka.common.ConsumerGroupState.toInternal() =
    when (this) {
        org.apache.kafka.common.ConsumerGroupState.UNKNOWN -> ConsumerGroupState.UNKNOWN
        org.apache.kafka.common.ConsumerGroupState.PREPARING_REBALANCE -> ConsumerGroupState.PREPARING_REBALANCE
        org.apache.kafka.common.ConsumerGroupState.COMPLETING_REBALANCE -> ConsumerGroupState.COMPLETING_REBALANCE
        org.apache.kafka.common.ConsumerGroupState.STABLE -> ConsumerGroupState.STABLE
        org.apache.kafka.common.ConsumerGroupState.DEAD -> ConsumerGroupState.DEAD
        org.apache.kafka.common.ConsumerGroupState.EMPTY -> ConsumerGroupState.EMPTY
    }
