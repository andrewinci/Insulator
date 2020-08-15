package insulator.lib.configuration

import arrow.core.Either
import arrow.core.extensions.fx
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import insulator.lib.configuration.model.Cluster
import insulator.lib.configuration.model.Configuration
import kotlinx.serialization.json.Json
import java.io.File

class ConfigurationRepo(private val json: Json, private val configPath: String = "${System.getProperty("user.home")}/.insulator.config") {

    private val callbacks = ArrayList<(Configuration) -> Unit>()

    fun getConfiguration(): Either<ConfigurationRepoException, Configuration> {
        if (!File(configPath).exists()) store(Configuration(emptyList()))
        return kotlin.runCatching { File(configPath).readText() }
            .fold({ it.right() }, { ConfigurationRepoException("Unable to load the file", it).left() })
            .flatMap {
                json.runCatching { parse(Configuration.serializer(), it) }
                    .fold({ it.right() }, { ConfigurationRepoException("Unable to load the configurations", it).left() })
            }
    }

    fun delete(cluster: Cluster) = Either.fx<ConfigurationRepoException, Unit> {
        (!getConfiguration()).clusters
            .map { it.guid to it }.filter { (guid, _) -> guid != cluster.guid }
            .map { it.second }
            .let { Configuration(it) }
            .also { !store(it) }
            .also { config -> callbacks.forEach { it(config) } }
    }

    fun store(cluster: Cluster) = Either.fx<ConfigurationRepoException, Unit> {
        val configuration = (!getConfiguration()).clusters
            .map { it.guid to it }.toMap().plus(cluster.guid to cluster)
            .map { it.value }
            .let { Configuration(it) }
        !store(configuration)
        callbacks.forEach { it(configuration) }
    }

    private fun store(configuration: Configuration) = kotlin.runCatching {
        File(configPath).writeText(json.stringify(Configuration.serializer(), configuration))
    }.fold({ right() }, { ConfigurationRepoException("Unable to store the configuration", it).left() })

    fun addNewClusterCallback(callback: (Configuration) -> Unit) = callbacks.add(callback)
}

class ConfigurationRepoException(message: String, base: Throwable) : Throwable(message, base)
