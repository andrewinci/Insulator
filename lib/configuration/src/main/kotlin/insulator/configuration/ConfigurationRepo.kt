package insulator.configuration

import arrow.core.Either
import arrow.core.computations.either
import insulator.configuration.model.Configuration
import insulator.configuration.model.InsulatorTheme
import insulator.helper.runCatchingE
import insulator.kafka.model.Cluster
import kotlinx.serialization.json.Json
import java.io.File

class ConfigurationRepo(private val configPath: String) {

    private val json: Json = Json { }
    private val callbacks = ArrayList<(Configuration) -> Unit>()

    suspend fun getConfiguration(): Either<ConfigurationRepoException, Configuration> = either {
        if (!File(configPath).exists()) store(Configuration(emptyList()))
        val textConfig = runCatchingE { File(configPath).readText() }
            .mapLeft { ConfigurationRepoException("Unable to load the file", it) }
            .bind()
        json.runCatchingE { decodeFromString(Configuration.serializer(), textConfig) }
            .mapLeft { ConfigurationRepoException("Unable to load the configurations", it) }
            .bind()
    }

    suspend fun delete(cluster: Cluster): Either<Throwable, Unit> = either {
        val config = getConfiguration().bind()
        config.clusters
            .map { it.guid to it }.filter { (guid, _) -> guid != cluster.guid }
            .map { it.second }
            .let { Configuration(it) }
            .also { store(it).bind() }
            .also { c -> callbacks.forEach { it(c) } }
    }

    suspend fun store(theme: InsulatorTheme): Either<ConfigurationRepoException, Unit> = either {
        val configuration = getConfiguration()
            .map { Configuration(theme = theme, clusters = it.clusters) }
            .bind()
        store(configuration)
            .bind()
    }

    suspend fun store(cluster: Cluster): Either<ConfigurationRepoException, Unit> = either {
        val currentConfiguration = getConfiguration().bind()
        val configuration = currentConfiguration.clusters
            .associateBy { it.guid }.plus(cluster.guid to cluster)
            .map { it.value }
            .let { currentConfiguration.copy(clusters = it) }
        store(configuration).bind()
        callbacks.forEach { it(configuration) }
    }

    private fun store(configuration: Configuration) = runCatchingE {
        File(configPath).writeText(json.encodeToString(Configuration.serializer(), configuration))
    }.mapLeft { ConfigurationRepoException("Unable to store the configuration", it) }

    fun addNewClusterCallback(callback: (Configuration) -> Unit) {
        callbacks.add(callback)
    }
}

class ConfigurationRepoException(message: String, base: Throwable) : Throwable(message, base)
