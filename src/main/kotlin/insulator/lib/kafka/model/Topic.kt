package insulator.lib.kafka.model

data class Topic(
    val name: String,
    val isInternal: Boolean? = null,
    val partitionCount: Int? = null,
    val messageCount: Long?
)
