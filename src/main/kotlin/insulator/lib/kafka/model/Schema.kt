package insulator.lib.kafka.model

data class Subject(val subject: String, val schemas: List<Schema>)
data class Schema(val schema: String, val version: Int)