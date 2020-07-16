package insulator.lib.kafka.model

data class Topic(val name: String,
                 val internal: Boolean? = null,
                 val partitions: Int?  = null)