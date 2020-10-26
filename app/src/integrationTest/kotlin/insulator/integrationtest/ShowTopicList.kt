package insulator.integrationtest

import insulator.integrationtest.helpers.FxFixture
import insulator.integrationtest.helpers.lookupFirst
import insulator.kafka.model.Cluster
import io.kotest.assertions.timing.eventually
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import javafx.scene.control.Button
import javafx.scene.control.Label
import kotlinx.coroutines.delay
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

@ExperimentalTime
class ShowTopicList : FreeSpec({
    "Show list of topics" - {
        FxFixture().use { fixture ->
            fixture.startAppWithKafkaCuster("Test topic list")

            "Title should be Clusters" {
                eventually(10.seconds) {
                    lookupFirst<Label>(".h1").text shouldBe "Clusters"
                }
            }
            //todo: complete
        }
    }
})
