package insulator.configuration.model

import insulator.kafka.model.Cluster
import kotlinx.serialization.Serializable

@Serializable
data class Configuration(
    var clusters: List<Cluster>,
    val theme: InsulatorTheme = InsulatorTheme.Light
)

enum class InsulatorTheme { Dark, Light }
