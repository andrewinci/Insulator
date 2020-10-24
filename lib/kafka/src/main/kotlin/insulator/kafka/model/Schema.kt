package insulator.kafka.model

data class Subject(val name: String, val schemas: List<Schema>)
data class Schema(val schema: String, val version: Int, val id: Int)
