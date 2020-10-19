package insulator.lib.configuration.model

import kotlinx.serialization.Serializable

@Serializable
data class Configuration(var clusters: List<Cluster>)
