package insulator.integrationtest.helpers

import javafx.scene.Node
import javafx.scene.control.Button
import javafx.stage.Screen
import org.testfx.api.FxAssert
import org.testfx.api.FxRobot
import org.testfx.api.FxService
import org.testfx.util.WaitForAsyncUtils
import java.nio.file.Path
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

fun screenShoot() = FxRobot()
    .capture(Screen.getPrimary().bounds)
    .let {
        FxService.serviceContext()
            .captureSupport
            .saveImage(it.image, Path.of("test.png"))
    }

fun waitFXThread() {
    WaitForAsyncUtils.waitForFxEvents()
}

@ExperimentalTime
suspend fun eventually(f: suspend () -> Unit) =
    io.kotest.assertions.timing.eventually(30.seconds, f)
