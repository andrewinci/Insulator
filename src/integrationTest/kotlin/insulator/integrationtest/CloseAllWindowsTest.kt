// package insulator.integrationtest
//
// import arrow.core.right
// import insulator.Insulator
// import insulator.integrationtest.helper.TestCluster
// import insulator.integrationtest.helper.cleanupFXFramework
// import insulator.integrationtest.helper.configureFXFramework
// import insulator.integrationtest.helper.configureIntegrationDi
// import insulator.integrationtest.helper.waitPrimaryStage
// import insulator.lib.configuration.ConfigurationRepo
// import insulator.lib.configuration.model.Configuration
// import insulator.lib.helpers.runOnFXThread
// import io.kotest.core.spec.style.FunSpec
// import io.kotest.matchers.shouldBe
// import io.mockk.every
// import io.mockk.just
// import io.mockk.mockk
// import io.mockk.runs
// import javafx.scene.control.Label
// import javafx.stage.Stage
// import org.testfx.api.FxRobot
// import tornadofx.* // ktlint-disable no-wildcard-imports
//
// class CloseAllWindowsTest : FunSpec({
//
//    test("Close all windows when the main one is closed") {
//        TestCluster().use { cluster ->
//            // arrange
//            val sut = Insulator()
//            val testTopicCount = 10
//            val topicPrefix = "test-topic"
//            cluster.createTopics(*(1..testTopicCount).map { "$topicPrefix$it" }.toTypedArray())
//            configureIntegrationDi(
//                ConfigurationRepo::class to mockk<ConfigurationRepo> {
//                    every { addNewClusterCallback(any()) } just runs
//                    every { getConfiguration() } returns
//                        Configuration(clusters = listOf(cluster.clusterConfiguration)).right()
//                }
//            )
//            with(FxRobot()) {
//                // act
//                sut.runOnFXThread { start(FX.getPrimaryStage()!!) }
//                val scene = waitPrimaryStage().scene
//                // click on the local cluster
//                clickOn("#label-cluster-name-${cluster.clusterConfiguration.guid}")
//
//                // assert
//                (scene.window as Stage).title shouldBe cluster.clusterConfiguration.name
//                lookup<Label> { it.text.startsWith(topicPrefix) }.queryAll<Label>().count() shouldBe testTopicCount
//            }
//        }
//    }
//
//    beforeTest { configureFXFramework() }
//    afterTest { cleanupFXFramework() }
// })
