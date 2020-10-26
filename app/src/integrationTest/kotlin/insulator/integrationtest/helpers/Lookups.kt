package insulator.integrationtest.helpers

import javafx.scene.Node
import org.testfx.api.FxAssert

fun <T : Node> lookupFirst(cssSelector: String) =
    lookup<T>(cssSelector).first()

fun <T : Node> lookup(cssSelector: String) =
    FxAssert.assertContext().nodeFinder.lookup(cssSelector).queryAll<T>()
