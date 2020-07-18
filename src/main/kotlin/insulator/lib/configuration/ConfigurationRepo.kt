package insulator.lib.configuration

import arrow.core.*
import arrow.core.extensions.fx
import com.google.gson.Gson
import insulator.lib.configuration.model.Cluster
import insulator.lib.configuration.model.Configuration
import java.io.File

class ConfigurationRepo(private val gson: Gson) {

    companion object {
        lateinit var currentCluster: Cluster
    }

    private val callbacks = ArrayList<(Configuration) -> Unit>()
    private val CONFIG_FILE_NAME = "${System.getProperty("user.home")}/.insulator.config"

    fun getConfiguration(): Either<ConfigurationRepoException, Configuration> {
        if (!File(CONFIG_FILE_NAME).exists()) store(Configuration(emptyList()))
        return kotlin.runCatching { File(CONFIG_FILE_NAME).readText() }
                .fold({ it.right() }, { ConfigurationRepoException("Unable to load the file", it).left() })
                .flatMap {
                    gson.runCatching { fromJson<Configuration>(it, Configuration::class.java) }
                            .fold({ it.right() }, { ConfigurationRepoException("Unable to load the configurations", it).left() })
                }
    }

    fun store(cluster: Cluster) = Either.fx<ConfigurationRepoException, Unit> {
        (!getConfiguration()).clusters
                .filterNotNull()
                .map { it.guid to it }.toMap().plus(cluster.guid to cluster)
                .map { it.value }
                .let { Configuration(it) }
                .also { !store(it) }
                .also { config -> callbacks.forEach { it(config) } }
    }

    private fun store(configuration: Configuration) = Either.fx<ConfigurationRepoException, Unit> {
        kotlin.runCatching { File(CONFIG_FILE_NAME).writeText(gson.toJson(configuration)) }
                .fold({ right() }, { ConfigurationRepoException("Unable to store the configuration", it).left() })
    }

    fun addNewClusterCallback(callback: (Configuration) -> Unit) = callbacks.add(callback)
}

class ConfigurationRepoException(message: String, base: Throwable) : Throwable(message, base)