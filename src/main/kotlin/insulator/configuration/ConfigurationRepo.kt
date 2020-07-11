package insulator.configuration

import insulator.model.Cluster

class ConfigurationRepo {
    private val clusters = ArrayList<Cluster>()
    private val callbacks = ArrayList<(Cluster) -> Unit>()

    fun getClusters() = clusters.toList()

    fun store(cluster: Cluster) {
        clusters.add(cluster).also { callbacks.forEach { it(cluster) } }
    }

    fun addCallback(callback: (Cluster) -> Unit) = callbacks.add(callback)
}