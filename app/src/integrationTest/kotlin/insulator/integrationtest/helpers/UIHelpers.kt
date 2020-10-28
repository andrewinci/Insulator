package insulator.integrationtest.helpers

import javafx.scene.Node
import javafx.scene.control.Button
import org.testfx.api.FxAssert
import org.testfx.api.FxRobot
import org.testfx.util.WaitForAsyncUtils
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.util.UUID
import javax.imageio.ImageIO
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

fun screenShoot(name: String = "") {
    val path = Paths.get("captures").also { it.toFile().mkdirs() }
    val image: BufferedImage = Robot().createScreenCapture(Rectangle(Toolkit.getDefaultToolkit().screenSize))
    val filePath = Path.of(path.toAbsolutePath().toString(), "$name-${UUID.randomUUID()}.png")
    ImageIO.write(image, "png", File(filePath.toString()))
}

fun waitFXThread() {
    WaitForAsyncUtils.waitForFxEvents()
}

@ExperimentalTime
suspend fun eventually(f: suspend () -> Unit) =
    io.kotest.assertions.timing.eventually(30.seconds, f)
