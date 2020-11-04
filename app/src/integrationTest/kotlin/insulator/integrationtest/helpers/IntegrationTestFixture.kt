package insulator.integrationtest.helpers

import insulator.Insulator
import insulator.configuration.ConfigurationRepo
import insulator.kafka.AdminApi
import insulator.kafka.adminApi
import insulator.kafka.local.SchemaRegistryContainer
import insulator.kafka.model.Cluster
import insulator.kafka.model.SchemaRegistryConfiguration
import insulator.kafka.model.Topic
import insulator.kafka.producer.Producer
import insulator.kafka.producer.stringProducer
import insulator.test.helper.deleteTestSandboxFolder
import insulator.test.helper.getTestSandboxFolder
import kotlinx.coroutines.delay
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testfx.api.FxToolkit
import tornadofx.FX
import java.io.Closeable

class IntegrationTestFixture : Closeable {
    private lateinit var adminApi: AdminApi
    lateinit var stringProducer: Producer
    private val currentHomeFolder = getTestSandboxFolder().toAbsolutePath()
    private val kafka = KafkaContainer()
    private val schemaRegistry = SchemaRegistryContainer().withKafka(kafka)
    lateinit var currentKafkaCluster: Cluster

    init {
        val stage = FxToolkit.registerPrimaryStage()
        FX.setPrimaryStage(stage = stage)
        // override home user to avoid messing up with current user configs
        System.setProperty("user.home", currentHomeFolder.toString())
    }

    suspend fun startAppWithKafkaCuster(clusterName: String, createSchemaRegistry: Boolean = true) {
        kafka.start()
        kafka.waitingFor(Wait.forListeningPort())
        currentKafkaCluster = Cluster(
            name = clusterName,
            endpoint = kafka.bootstrapServers,
            schemaRegistryConfig = if (createSchemaRegistry) {
                schemaRegistry.start()
                schemaRegistry.waitingFor(Wait.forListeningPort())
                SchemaRegistryConfiguration(schemaRegistry.endpoint)
            } else SchemaRegistryConfiguration()
        )
        startApp(currentKafkaCluster)
        adminApi = adminApi(currentKafkaCluster)
        stringProducer = stringProducer(currentKafkaCluster)
    }

    suspend fun startApp(vararg clusters: Cluster) {
        storeConfiguration(*clusters)
        FxToolkit.setupApplication(Insulator::class.java)
        // wait a bit, CI may be slow
        waitFXThread()
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

    suspend fun createTopic(s: String) = adminApi.createTopics(Topic(s, partitionCount = 3, replicationFactor = 1))
}
