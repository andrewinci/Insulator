package insulator.integrationtest.helpers

import javafx.scene.Node
import javafx.scene.control.Button
import org.testfx.api.FxAssert
import org.testfx.api.FxRobot
import org.testfx.util.WaitForAsyncUtils
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

fun clickOkOnDialog() {
    FxAssert.assertContext().nodeFinder
        .lookup(".dialog-pane.alert")
        .lookup(".button")
        .lookup<Button> { btn -> btn.text == "OK" }
        .queryButton()
        .click()
    waitFXThread()
}

fun Node.click() {
    FxRobot().clickOn(this)
    waitFXThread()
}

fun waitFXThread() {
    WaitForAsyncUtils.waitForFxEvents()
}

@ExperimentalTime
suspend fun eventually(f: suspend () -> Unit) =
    io.kotest.assertions.timing.eventually(30.seconds, f)
