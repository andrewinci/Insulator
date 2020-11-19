package insulator.integrationtest.helpers

import javafx.scene.Node
import javafx.stage.Stage
import javafx.stage.Window
import org.testfx.api.FxAssert
import tornadofx.Rendered
import kotlin.time.ExperimentalTime

private val nodeFinder = FxAssert.assertContext().nodeFinder

fun getPrimaryWindow(): Node = nodeFinder.rootNode(Window.getWindows().first())

@ExperimentalTime
suspend fun waitWindowWithTitle(title: String): Node = eventually {
    val windows = Window.getWindows().map { (it as? Stage) }
    nodeFinder.rootNode(
        windows.firstOrNull { it?.title == title }
            ?: throw AssertionError("There is no window in the scene-graph matching the title $title. Other windows are: ${windows.map { it?.title }} ")
    )
}

fun <T : Node> Node.lookupAny(cssRule: Rendered): MutableSet<T> = lookupAny(cssRule.render())
fun <T : Node> Node.lookupAny(cssSelector: String): MutableSet<T> =
    FxAssert.assertContext().nodeFinder.runCatching {
        from(this@lookupAny).lookup(cssSelector).queryAll<T>()
    }.fold({ it }, { throw AssertionError("There is no node in the scene-graph matching: $cssSelector") })

@ExperimentalTime
suspend fun <T : Node> Node.lookupFirst(cssRule: Rendered): T = lookupFirst(cssRule.render())

@ExperimentalTime
suspend fun <T : Node> Node.lookupFirst(cssSelector: String): T = eventually {
    lookupAny<T>(cssSelector).firstOrNull()
        ?: throw AssertionError("There is no node in the scene-graph matching: $cssSelector")
}
