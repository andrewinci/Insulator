package insulator.integrationtest.helpers

import insulator.Insulator
import insulator.configuration.ConfigurationRepo
import insulator.kafka.model.Cluster
import insulator.kafka.model.SchemaRegistryConfiguration
import insulator.test.helper.deleteTestSandboxFolder
import insulator.test.helper.getTestSandboxFolder
import kotlinx.coroutines.delay
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testfx.api.FxToolkit
import tornadofx.FX
import java.io.Closeable

class FxFixture() : Closeable {
    private val currentHomeFolder = getTestSandboxFolder().toAbsolutePath()
    private val kafka = KafkaContainer()
    private val schemaRegistry = SchemaRegistryContainer().withKafka(kafka)

    init {
        val stage = FxToolkit.registerPrimaryStage()
        FX.setPrimaryStage(stage = stage)
        // override home user to avoid messing up with current user configs
        System.setProperty("user.home", currentHomeFolder.toString())
    }

    suspend fun startAppWithKafkaCuster(clusterName: String, createSchemaRegistry: Boolean = true) {
        kafka.start()
        kafka.waitingFor(Wait.forListeningPort())
        startApp(
            Cluster(
                name = clusterName,
                endpoint = kafka.bootstrapServers,
                schemaRegistryConfig = if (createSchemaRegistry) {
                    schemaRegistry.start()
                    schemaRegistry.waitingFor(Wait.forListeningPort())
                    SchemaRegistryConfiguration(schemaRegistry.endpoint)
                } else SchemaRegistryConfiguration()
            )
        )
    }

    suspend fun startApp(vararg clusters: Cluster) {
        storeConfiguration(*clusters)
        FxToolkit.setupApplication(Insulator::class.java)
        // wait a bit, CI may be slow
        waitFXThread()
        delay(5_000)
    }

    private suspend fun storeConfiguration(vararg cluster: Cluster) =
        ConfigurationRepo("$currentHomeFolder/.insulator.config").let { repo ->
            cluster.forEach { repo.store(it) }
        }

    override fun close() {
        waitFXThread() // wait any pending task
        kotlin.runCatching { FxToolkit.cleanupStages() }
        kotlin.runCatching { FxToolkit.cleanupApplication(FX.application) }
        deleteTestSandboxFolder()
        kafka.runCatching { stop(); close() }
        schemaRegistry.runCatching { stop(); close() }
    }
}
