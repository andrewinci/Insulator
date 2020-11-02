package insulator.integrationtest.helpers

import javafx.application.Platform
import javafx.embed.swing.SwingFXUtils
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.stage.Stage
import javafx.stage.Window
import kotlinx.coroutines.delay
import org.testfx.api.FxAssert
import org.testfx.api.FxRobot
import org.testfx.util.WaitForAsyncUtils
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.util.UUID
import javax.imageio.ImageIO
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

@ExperimentalTime
suspend fun clickOkOnDialog() = eventually {
    FxAssert.assertContext().nodeFinder
        .lookup(".dialog-pane.alert")
        .lookup(".button")
        .lookup<Button> { btn -> btn.text == "OK" }
        .queryButton()
        .click()
    waitFXThread()
}

suspend fun Node.doubleClick() {
    FxRobot().doubleClickOn(this)
    waitFXThread()
    delay(2_000)
}

suspend fun Node.click() {
    FxRobot().clickOn(this)
    waitFXThread()
    delay(2_000)
}

fun screenShoot(name: String = "") {
    val path = Paths.get("captures").also { it.toFile().mkdirs() }
    Platform.runLater {
        Window.getWindows()
            .map { (it as Stage).scene.snapshot(null) }
            .map { SwingFXUtils.fromFXImage(it, null) }
            .forEach {
                val filePath = Path.of(path.toAbsolutePath().toString(), "$name-${UUID.randomUUID()}.png")
                ImageIO.write(it, "png", File(filePath.toString()))
            }
    }
}

fun waitFXThread() {
    WaitForAsyncUtils.waitForFxEvents()
}

@ExperimentalTime
suspend fun <T> eventually(f: suspend () -> T): T =
    io.kotest.assertions.timing.eventually(60.seconds, f)
