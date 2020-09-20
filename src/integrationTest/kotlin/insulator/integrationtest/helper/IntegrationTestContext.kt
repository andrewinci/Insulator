package insulator.integrationtest.helper

import insulator.lib.configuration.model.Cluster
import insulator.lib.configuration.model.SchemaRegistryConfiguration
import io.confluent.kafka.schemaregistry.avro.AvroSchema
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient
import io.confluent.kafka.schemaregistry.client.rest.RestService
import javafx.application.Application
import javafx.stage.Stage
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.koin.core.context.stopKoin
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testfx.api.FxRobot
import org.testfx.api.FxToolkit
import tornadofx.* // ktlint-disable no-wildcard-imports
import java.io.Closeable
import java.io.File
import kotlin.reflect.KClass

class IntegrationTestContext(createKafkaCluster: Boolean = true, createSchemaRegistry: Boolean = true) : FxRobot(), Closeable {
    private val mockUserHome = System.getProperty("java.io.tmpdir")
    private val kafka = KafkaContainer()
    private val schemaRegistry = SchemaRegistryContainer().withKafka(kafka)
    lateinit var clusterConfiguration: Cluster

    init {
        System.setProperty("user.home", mockUserHome)
        if (createKafkaCluster) {
            kafka.start()
            kafka.waitingFor(Wait.forListeningPort())
            clusterConfiguration = Cluster.empty().copy(
                name = "Test local cluster",
                endpoint = kafka.bootstrapServers,
                schemaRegistryConfig = if (createSchemaRegistry) {
                    schemaRegistry.start()
                    schemaRegistry.waitingFor(Wait.forListeningPort())
                    SchemaRegistryConfiguration(schemaRegistry.endpoint)
                } else null
            )
        }
    }

    fun createTopics(vararg name: String) {
        val admin = AdminClient.create(
            mapOf(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG to kafka.bootstrapServers
            ).toProperties()
        )
        admin.createTopics(name.map { NewTopic(it, 3, 1) })
    }

    fun createSchema(name: String, content: String) {
        val restService = RestService(schemaRegistry.endpoint)
        val client = CachedSchemaRegistryClient(restService, 1000)
        client.register(name, AvroSchema(content))
    }

    fun startApp(applicationClass: Class<out Application>) {
        FxToolkit.registerPrimaryStage()
        FxToolkit.setupApplication(applicationClass)
        waitPrimaryStage()
    }

    fun configureDi(vararg dependencyMap: Pair<KClass<*>, Any>) {
        if (FX.dicontainer != null) throw TestHelperError("DI already configured")
        FX.dicontainer = object : DIContainer {
            val main = insulator.di.DIContainer()

            @Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
            override fun <T : Any> getInstance(type: KClass<T>): T =
                dependencyMap.toMap()[type] as? T ?: main.getInstance(type)
        }
    }

    fun <T : Any> getInstance(clazz: KClass<T>): T = FX.dicontainer!!.getInstance(clazz)

    private fun waitPrimaryStage(limit: Int = 20): Stage {
        repeat(limit) {
            val primaryStage = FX.getPrimaryStage()
            if (primaryStage?.isShowing == true && primaryStage.isFocused) {
                Thread.sleep(1000)
                return primaryStage
            }
            Thread.sleep(1000)
        }
        throw TestHelperError("Timeout waiting for primary stage to show up")
    }

    private fun deleteConfig() {
        val configPath = File("$mockUserHome.insulator.config")
        if (configPath.exists()) configPath.delete()
    }

    override fun close() {
        kotlin.runCatching { FxToolkit.cleanupStages() }
        kotlin.runCatching { FxToolkit.cleanupApplication(FX.application) }
        FX.dicontainer = null
        stopKoin()
        deleteConfig()
        kafka.close()
        schemaRegistry.close()
    }
}

class TestHelperError(message: String) : Throwable(message)
