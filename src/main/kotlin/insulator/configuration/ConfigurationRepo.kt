package insulator.configuration

import arrow.core.*
import arrow.core.extensions.fx
import com.google.gson.Gson
import insulator.model.Cluster
import java.io.File

data class Configuration(var clusters: List<Cluster>)

class ConfigurationRepo(private val gson: Gson) {
    private val callbacks = ArrayList<(Cluster) -> Unit>()
    private val CONFIG_FILE_NAME = ".insulator.config"

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
        val newConfiguration = !getConfiguration()
        newConfiguration.clusters += cluster
        !store(newConfiguration)
        callbacks.forEach { it(cluster) }
    }

    private fun store(configuration: Configuration) = Either.fx<ConfigurationRepoException, Unit> {
        kotlin.runCatching { File(CONFIG_FILE_NAME).writeText(gson.toJson(configuration)) }
                .fold({ right() }, { ConfigurationRepoException("Unable to store the configuration", it).left() })
    }

    fun addNewClusterCallback(callback: (Cluster) -> Unit) = callbacks.add(callback)

    companion object {
        lateinit var currentCluster: Cluster
    }
}

class ConfigurationRepoException(message: String, base: Throwable) : Throwable(message, base)