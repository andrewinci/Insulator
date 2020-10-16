package insulator.lib.kafka.model

data class Record(
    val key: String?,
    val value: String,
    val timestamp: Long,
    val headers: Map<String, ByteArray>,
)
