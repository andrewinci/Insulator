package insulator.integrationtest.helpers

import javafx.scene.Node
import org.testfx.api.FxAssert
import org.testfx.api.FxRobot
import org.testfx.api.FxRobotInterface
import tornadofx.CssRule
import tornadofx.CssRuleSet

fun <T : Node> lookupFirst(cssSelector: String): T =
    lookup<T>(cssSelector).firstOrNull()
        ?: throw AssertionError("There is no node in the scene-graph matching: $cssSelector")

fun <T : Node> lookupFirst(cssRule: CssRule): T = lookupFirst(cssRule.render())
fun <T : Node> lookupFirst(cssRule: CssRuleSet): T = lookupFirst(cssRule.render())

fun <T : Node> lookup(cssSelector: String): MutableSet<T> = FxAssert.assertContext().nodeFinder.lookup(cssSelector).queryAll()
fun <T : Node> lookup(cssRule: CssRule): MutableSet<T> = lookup(cssRule.name)

fun Node.click(): FxRobotInterface = FxRobot().clickOn(this)
