package insulator.integrationtest.helpers

import insulator.Insulator
import insulator.configuration.ConfigurationRepo
import insulator.kafka.AdminApi
import insulator.kafka.adminApi
import insulator.kafka.model.Cluster
import insulator.kafka.model.SchemaRegistryConfiguration
import insulator.kafka.model.Topic
import insulator.kafka.producer.Producer
import insulator.kafka.producer.stringProducer
import insulator.test.helper.deleteTestSandboxFolder
import insulator.test.helper.getTestSandboxFolder
import kotlinx.coroutines.delay
import org.testfx.api.FxToolkit
import tornadofx.FX
import java.io.Closeable

class IntegrationTestFixture : Closeable {
    private lateinit var adminApi: AdminApi
    lateinit var stringProducer: Producer
    private val currentHomeFolder = getTestSandboxFolder().toAbsolutePath()
    lateinit var currentKafkaCluster: Cluster

    init {
        val stage = FxToolkit.registerPrimaryStage()
        FX.setPrimaryStage(stage = stage)
        // override home user to avoid messing up with current user configs
        System.setProperty("user.home", currentHomeFolder.toString())
    }

    suspend fun startAppWithKafkaCuster(clusterName: String, createSchemaRegistry: Boolean = true) {
        currentKafkaCluster = Cluster(
            name = clusterName,
            endpoint = "PLAINTEXT://127.0.0.1:9092",
            schemaRegistryConfig = SchemaRegistryConfiguration() // todo: add
        )
        startApp(currentKafkaCluster)
        adminApi = adminApi(currentKafkaCluster)
        stringProducer = stringProducer(currentKafkaCluster)
        delay(15_000)
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
    }

    suspend fun createTopic(s: String) = adminApi.createTopics(Topic(s, partitionCount = 3, replicationFactor = 1))
}
