package insulator.integrationtest

import insulator.Insulator
import insulator.integrationtest.helper.IntegrationTestContext
import insulator.lib.helpers.runOnFXThread
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
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
            context.configureDi(context.clusterConfiguration)

            // act
            context.startApp(Insulator::class.java)
            // click on the local cluster to show the list of topics
            context.clickOn(".cluster")
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
            FX.getApplication() shouldBe null
        }
    }
})
