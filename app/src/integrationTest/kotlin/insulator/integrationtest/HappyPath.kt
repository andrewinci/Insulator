package insulator.integrationtest

import insulator.integrationtest.helpers.FxFixture
import insulator.integrationtest.helpers.lookupFirst
import insulator.kafka.model.Cluster
import io.kotest.assertions.timing.eventually
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import javafx.scene.control.Button
import javafx.scene.control.Label
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

@ExperimentalTime
class HappyPath : FreeSpec({
    "Happy path start the app and show list clusters view" - {
        FxFixture().use { fixture ->
            val clusters = (1..5).map { Cluster(name = "clusterName$it", endpoint = "endpoint$it") }.toTypedArray()
            fixture.startAppWithClusters(*clusters)

            "Title should be Clusters" {
                eventually(10.seconds) {
                    lookupFirst<Label>(".h1").text shouldBe "Clusters"
                }
            }

            "All clusters config should be available with a settings button" {
                clusters.forEach {
                    lookupFirst<Label>("#cluster-${it.guid} .h2").text shouldBe it.name
                    lookupFirst<Label>("#cluster-${it.guid} .sub-title").text shouldBe it.endpoint
                    lookupFirst<Button>("#cluster-${it.guid} .settings-button")
                }
            }

            "There is a button to add new clusters" {
                lookupFirst<Button>(".button-bar .button").text shouldBe "Add new cluster"
            }
        }
    }
})
