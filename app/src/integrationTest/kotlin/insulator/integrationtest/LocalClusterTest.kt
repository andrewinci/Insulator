package insulator.integrationtest

import insulator.integrationtest.helpers.click
import insulator.integrationtest.helpers.getPrimaryWindow
import insulator.integrationtest.helpers.lookupFirst
import insulator.integrationtest.helpers.screenShoot
import javafx.scene.control.Button
import kotlinx.coroutines.delay
import tornadofx.CssRule
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

@ExperimentalTime
class LocalClusterTest : GenericMainViewTest(
    "Local Cluster",
    {
        it.startApp()
        getPrimaryWindow().lookupFirst<Button>(CssRule.id("button-local-cluster")).click()
        screenShoot("starting-local-cluster")
        delay(30.seconds)
    }
)
