package insulator.integrationtest

import arrow.core.right
import insulator.Insulator
import insulator.integrationtest.helper.IntegrationTestContext
import insulator.lib.configuration.ConfigurationRepo
import insulator.lib.configuration.model.Configuration
import insulator.lib.helpers.runOnFXThread
import io.kotest.core.spec.style.FunSpec
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import javafx.stage.Stage
import javafx.stage.WindowEvent
import org.testfx.util.WaitForAsyncUtils.waitForFxEvents
import tornadofx.FX

class CloseAllWindowsTest : FunSpec({

    test("Close application when the main one is closed") {
        IntegrationTestContext().use { context ->
            // arrange
            val topicPrefix = "test-topic"
            val topics = (1..10).map { "$topicPrefix-$it" }
            topics.forEach { context.createTopics(it) }
            context.configureDi(
                ConfigurationRepo::class to mockk<ConfigurationRepo> {
                    every { addNewClusterCallback(any()) } just runs
                    every { getConfiguration() } returns
                        Configuration(clusters = listOf(context.clusterConfiguration)).right()
                }
            )

            // act
            context.startApp(Insulator::class.java)
            // click on the local cluster to show the list of topics
            context.clickOn("#cluster-${context.clusterConfiguration.guid}")
            waitForFxEvents()

            // open the test-topic-1 view
            FX.primaryStage.runOnFXThread { toFront() }; waitForFxEvents()
            context.doubleClickOn("#topic-$topicPrefix-1"); waitForFxEvents()

            // open the test-topic-2 view
            FX.primaryStage.runOnFXThread { toFront() }; waitForFxEvents()
            context.doubleClickOn("#topic-$topicPrefix-2"); waitForFxEvents()

            // close main window
            (FX.primaryStage.scene.window as Stage).runOnFXThread {
                fireEvent(WindowEvent(this, WindowEvent.WINDOW_CLOSE_REQUEST))
            }; waitForFxEvents()

            // assert
            // FX.getApplication() shouldBe null
        }
    }
})
