package insulator.integrationtest.helpers

import insulator.Insulator
import insulator.configuration.ConfigurationRepo
import insulator.configuration.model.InsulatorTheme
import insulator.kafka.AdminApi
import insulator.kafka.SchemaRegistry
import insulator.kafka.adminApi
import insulator.kafka.local.SchemaRegistryContainer
import insulator.kafka.model.Cluster
import insulator.kafka.model.SchemaRegistryConfiguration
import insulator.kafka.model.Topic
import insulator.kafka.model.TopicConfiguration
import insulator.kafka.producer.Producer
import insulator.kafka.producer.stringProducer
import insulator.kafka.schemaRegistry
import insulator.test.helper.deleteTestSandboxFolder
import insulator.test.helper.getTestSandboxFolder
import kotlinx.coroutines.runBlocking
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testfx.api.FxToolkit
import tornadofx.FX
import java.io.Closeable
import kotlin.time.ExperimentalTime
import org.testcontainers.containers.Network
import org.testcontainers.utility.DockerImageName

private val kafka: KafkaContainer by lazy {
    val res = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.4.3"))
    res.waitingFor(Wait.forListeningPort()).start()
    res
}

private val schemaRegistryContainer: SchemaRegistryContainer by lazy {
    val res = SchemaRegistryContainer().withKafka(kafka)
    res.waitingFor(Wait.forListeningPort())!!.start()
    res
}

@ExperimentalTime
class IntegrationTestFixture : Closeable {
    private var adminApi: AdminApi? = null
    private var schemaRegistry: SchemaRegistry? = null
    lateinit var stringProducer: Producer
    private val currentHomeFolder = getTestSandboxFolder().toAbsolutePath()

    lateinit var currentKafkaCluster: Cluster

    init {
        val stage = FxToolkit.registerPrimaryStage()
        FX.setPrimaryStage(stage = stage)
        // override home user to avoid messing up with current user configs
        System.setProperty("user.home", currentHomeFolder.toString())
    }

    fun startAppWithKafkaCuster(clusterName: String, createSchemaRegistry: Boolean = true) {
        runBlocking {
            currentKafkaCluster = Cluster(
                name = clusterName,
                endpoint = kafka.bootstrapServers,
                schemaRegistryConfig = if (createSchemaRegistry) {
                    SchemaRegistryConfiguration(schemaRegistryContainer.endpoint)
                } else SchemaRegistryConfiguration()
            )
            startApp(currentKafkaCluster)
            if (createSchemaRegistry) schemaRegistry = schemaRegistry(currentKafkaCluster)
            adminApi = adminApi(currentKafkaCluster)
            stringProducer = stringProducer(currentKafkaCluster)
        }
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
            repo.store(InsulatorTheme.Dark)
        }

    override fun close() {
        waitFXThread() // wait any pending task
        kotlin.runCatching { FxToolkit.cleanupStages() }
        kotlin.runCatching { FxToolkit.cleanupApplication(FX.application) }
        deleteTestSandboxFolder()
        cleanUpKafka()
    }

    private fun cleanUpKafka() {
        runBlocking {
            // delete all topics
            adminApi?.listTopics()?.fold({ throw it }, { it })?.forEach { adminApi?.deleteTopic(it) }
            // delete all consumer groups
            adminApi?.listConsumerGroups()?.fold({ throw it }, { it })?.forEach { adminApi?.deleteConsumerGroup(it) }
            // delete all schemas
            schemaRegistry?.getAllSubjects()?.map { it.forEach { schema -> schemaRegistry?.deleteSubject(schema) } }
        }
    }

    suspend fun createCompactedTopic(s: String) = adminApi!!.createTopics(
        Topic(
            s,
            partitionCount = 1,
            replicationFactor = 1,
            isCompacted = true,
            configuration = TopicConfiguration(
                mapOf(
                    "delete.retention.ms" to "0",
                    "min.cleanable.dirty.ratio" to "0.001",
                    "segment.ms" to "100",
                )
            )
        )
    )

    suspend fun createTopic(s: String) = adminApi!!.createTopics(Topic(s, partitionCount = 3, replicationFactor = 1))
    fun createTestSchema(schemaName: String) = schemaRegistry!!.register(schemaName, testSchema(5))
    fun createTestSchemaUpdate(schemaName: String) = schemaRegistry!!.register(schemaName, testSchema(4))

    fun testSchema(i: Int = 5) =
        """
        {
          "type": "record", 
          "name": "value_test_schema", 
          "namespace": "com.mycorp.mynamespace", 
          "doc": "Sample schema to help you get started.", 
          "fields": [{
              "name": "test", 
              "type": ["null", {
                  "type": "bytes", 
                  "logicalType": "decimal", 
                  "precision": $i, 
                  "scale": 2
                }], 
              "default": null
            }]
        }
        """.trimIndent()
}
