package insulator.integrationtest.helpers

import javafx.scene.Node
import org.testfx.api.FxAssert
import org.testfx.api.FxRobot
import org.testfx.api.FxRobotInterface

fun <T : Node> lookupFirst(cssSelector: String) =
    lookup<T>(cssSelector).first()

fun <T : Node> lookup(cssSelector: String) =
    FxAssert.assertContext().nodeFinder.lookup(cssSelector).queryAll<T>()

fun Node.click(): FxRobotInterface = FxRobot().clickOn(this)