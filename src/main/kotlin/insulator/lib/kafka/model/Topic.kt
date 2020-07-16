package insulator.lib.kafka.model

data class Topic(val name: String,
                 val messageCount: Int? = 2,
                 val internal: Boolean? = null,
                 val partitions: Int?  = null)