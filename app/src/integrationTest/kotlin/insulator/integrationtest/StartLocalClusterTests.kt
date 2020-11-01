package insulator.integrationtest

import insulator.integrationtest.helpers.FxFixture
import insulator.integrationtest.helpers.click
import insulator.integrationtest.helpers.eventually
import insulator.integrationtest.helpers.lookupFirst
import insulator.integrationtest.helpers.mainWindow
import insulator.integrationtest.helpers.screenShoot
import insulator.integrationtest.helpers.waitWindowWithTitle
import insulator.ui.style.MainViewStyle
import insulator.ui.style.TextStyle.Companion.h1
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import javafx.scene.control.Button
import javafx.scene.control.Label
import kotlinx.coroutines.delay
import tornadofx.CssRule
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

@OptIn(ExperimentalTime::class)
class StartLocalClusterTests : FreeSpec({

    "Local cluster tests" - {
        FxFixture().use { fixture ->
            fixture.startApp()

            // Start local cluster
            mainWindow().lookupFirst<Button>(CssRule.id("button-local-cluster")).click()
            screenShoot("starting-local-cluster")
            delay(30.seconds)

            // Wait for the main view to show up
            val mainView = waitWindowWithTitle("Insulator")
            screenShoot("local-cluster-main-view")

            "Cluster name is shown in the sidebar" {
                eventually {
                    mainView.lookupFirst<Label>(MainViewStyle.sidebar.contains(h1)).text shouldBe "Local Cluster"
                }
            }

            "Show cluster details" {
                // todo
            }

            "Change cluster and reopend, doesn't create a new local cluster" {
                // todo
            }
        }
    }
})
