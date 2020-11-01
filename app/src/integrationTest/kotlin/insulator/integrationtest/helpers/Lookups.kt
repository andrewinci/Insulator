package insulator.integrationtest.helpers

import javafx.scene.Node
import javafx.stage.Stage
import javafx.stage.Window
import org.testfx.api.FxAssert
import tornadofx.FX
import tornadofx.Rendered
import kotlin.time.ExperimentalTime

private val nodeFinder = FxAssert.assertContext().nodeFinder
fun mainWindow(): Node = FX.primaryStage.scene.root

@OptIn(ExperimentalTime::class)
suspend fun waitWindowWithTitle(title: String): Node {
    val mainView = {
        nodeFinder.rootNode(
            Window.getWindows().firstOrNull { (it as? Stage)?.title == title }
                ?: throw AssertionError("There is no window in the scene-graph matching the title $title")
        )
    }
    eventually { mainView() }
    return mainView()
}

fun <T : Node> Node.lookupAny(cssRule: Rendered): MutableSet<T> = lookupAny(cssRule.render())
fun <T : Node> Node.lookupAny(cssSelector: String): MutableSet<T> =
    FxAssert.assertContext().nodeFinder.from(this).lookup(cssSelector).queryAll()

fun <T : Node> Node.lookupFirst(cssRule: Rendered): T = lookupFirst(cssRule.render())
fun <T : Node> Node.lookupFirst(cssSelector: String): T =
    lookupAny<T>(cssSelector).firstOrNull()
        ?: throw AssertionError("There is no node in the scene-graph matching: $cssSelector")
