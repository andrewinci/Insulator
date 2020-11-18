package insulator.kafka.model

import org.apache.kafka.common.config.TopicConfig

data class Topic(
    val name: String,
    val isInternal: Boolean? = null,
    val partitionCount: Int = 0,
    val messageCount: Long? = null,
    val replicationFactor: Short = 0,
    val isCompacted: Boolean = false,
    val configuration: TopicConfiguration = TopicConfiguration.empty()
) {
    companion object {
        fun empty() = Topic("")
    }
}

data class TopicConfiguration(
    val rawConfiguration: Map<String, String>
) {
    val isCompacted = rawConfiguration[TopicConfig.CLEANUP_POLICY_CONFIG] == TopicConfig.CLEANUP_POLICY_COMPACT

    companion object {
        fun empty() = TopicConfiguration(emptyMap())
    }
}
