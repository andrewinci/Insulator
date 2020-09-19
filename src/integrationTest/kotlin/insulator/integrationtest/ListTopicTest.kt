package insulator.integrationtest

import arrow.core.right
import insulator.Insulator
import insulator.integrationtest.helper.TestCluster
import insulator.integrationtest.helper.cleanupFXFramework
import insulator.integrationtest.helper.configureFXFramework
import insulator.integrationtest.helper.configureIntegrationDi
import insulator.integrationtest.helper.waitPrimaryStage
import insulator.lib.configuration.ConfigurationRepo
import insulator.lib.configuration.model.Configuration
import insulator.lib.helpers.runOnFXThread
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import javafx.scene.control.Label
import javafx.stage.Stage
import org.testfx.api.FxRobot
import tornadofx.* // ktlint-disable no-wildcard-imports

class ListTopicTest : FunSpec({

    test("Show the list of topic") {
        TestCluster().use { cluster ->
            // arrange
            val sut = Insulator()
            val topicPrefix = "test-topic"
            val topics = (1..10).map { "$topicPrefix$it" }
            topics.forEach { cluster.createTopics(it) }
            configureIntegrationDi(
                ConfigurationRepo::class to mockk<ConfigurationRepo> {
                    every { addNewClusterCallback(any()) } just runs
                    every { getConfiguration() } returns
                        Configuration(clusters = listOf(cluster.clusterConfiguration)).right()
                }
            )
            with(FxRobot()) {
                // act
                sut.runOnFXThread { start(FX.getPrimaryStage()!!) }
                val scene = waitPrimaryStage().scene
                // click on the local cluster to show the list of topics
                clickOn("#label-cluster-name-${cluster.clusterConfiguration.guid}")
                sleep(1000) // wait a bit to load the list of topics

                // assert
                val labels = lookup<Label> { it.text.startsWith(topicPrefix) }.queryAll<Label>()
                (scene.window as Stage).title shouldBe cluster.clusterConfiguration.name
                labels.map { it.text }.toSet() shouldBe topics.toSet()
            }
        }
    }

    beforeTest { configureFXFramework() }
    afterTest { cleanupFXFramework() }
})
