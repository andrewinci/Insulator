package insulator.integrationtest

import insulator.integrationtest.helpers.FxFixture
import insulator.integrationtest.helpers.eventually
import insulator.integrationtest.helpers.lookupFirst
import insulator.integrationtest.helpers.mainWindow
import insulator.ui.style.TextStyle
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import javafx.scene.control.Label
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class StringProducerTests : FreeSpec({

    "Produce a string message" {
        FxFixture().use { fixture ->
            fixture.startApp()

            // Start local cluster
            eventually {
                mainWindow()
                    .lookupFirst<Label>(TextStyle.h1).text shouldBe "Clusters"
            }
            
        }
    }
})
