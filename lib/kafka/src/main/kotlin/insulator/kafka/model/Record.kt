package insulator.kafka.model

data class Record(
    val key: String?,
    val value: String,
    val timestamp: Long,
    val headers: Map<String, List<ByteArray>>,
    val partition: Int,
    val offset: Long
)