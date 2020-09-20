package insulator.integrationtest.helper

import insulator.lib.configuration.model.Cluster
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
import kotlin.reflect.KClass

class IntegrationTestContext(createKafkaCluster: Boolean = true) : FxRobot(), Closeable {

    private val kafka = KafkaContainer()
    lateinit var clusterConfiguration: Cluster

    init {
        if (createKafkaCluster) {
            kafka.start()
            kafka.waitingFor(Wait.forListeningPort())
            clusterConfiguration = Cluster.empty().copy(name = "Test local cluster", endpoint = kafka.bootstrapServers)
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

    override fun close() {
        kafka.close()
        FxToolkit.cleanupStages()
        FxToolkit.cleanupApplication(FX.application)
        FX.dicontainer = null
        stopKoin()
    }
}

class TestHelperError(message: String) : Throwable(message)
