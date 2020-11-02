package insulator.integrationtest

import insulator.helper.runOnFXThread
import insulator.integrationtest.helpers.IntegrationTestFixture
import insulator.integrationtest.helpers.click
import insulator.integrationtest.helpers.doubleClick
import insulator.integrationtest.helpers.eventually
import insulator.integrationtest.helpers.getPrimaryWindow
import insulator.integrationtest.helpers.lookupAny
import insulator.integrationtest.helpers.lookupFirst
import insulator.integrationtest.helpers.screenShoot
import insulator.integrationtest.helpers.waitWindowWithTitle
import insulator.ui.style.MainViewStyle
import insulator.ui.style.TextStyle
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import tornadofx.CssRule
import tornadofx.Stylesheet
import tornadofx.Stylesheet.Companion.listView
import tornadofx.Stylesheet.Companion.textField
import kotlin.time.ExperimentalTime

@ExperimentalTime
class MainViewTest : GenericMainViewTest(
    "Test cluster",
    {
        it.startAppWithKafkaCuster("Test cluster")
        eventually {
            getPrimaryWindow()
                .lookupFirst<Node>(CssRule.id("cluster-${it.currentKafkaCluster.guid}"))
                .doubleClick()
        }
    }
)

@ExperimentalTime
abstract class GenericMainViewTest(clusterName: String, initialize: suspend (IntegrationTestFixture) -> Unit) : FreeSpec({

    "Main view $clusterName" - {
        IntegrationTestFixture().use { fixture ->
            val topicName = "test-new-topic"
            initialize(fixture)
            val mainView = waitWindowWithTitle("Insulator")
            screenShoot("main-view")

            "Cluster name is shown in the sidebar" {
                mainView.lookupFirst<Label>(MainViewStyle.sidebar.contains(TextStyle.h1)).text shouldBe clusterName
            }

            "Show cluster details" {
                mainView.lookupFirst<Button>(CssRule.id("button-cluster-info")).click()
                val clusterInfoWindow = waitWindowWithTitle(clusterName)
                screenShoot("local-cluster-info-window")

                with(clusterInfoWindow.lookupAny<Button>(Stylesheet.button).firstOrNull { it.text == "Close" }) {
                    this shouldNotBe null
                    this!!.click()
                }
            }

            "Create new topic" {
                // Click create topic button
                mainView.lookupFirst<Button>(CssRule.id("button-create-topic")).click()
                // Set topic name and other fields
                with(waitWindowWithTitle("Create new topic")) {
                    mapOf(
                        "field-create-topic-name" to topicName,
                        "field-create-topic-number-of-partitions" to "3",
                        "field-create-topic-replication-factor" to "1"
                    ).map { (fieldId, value) ->
                        lookupFirst<TextField>(CssRule.id(fieldId)).textProperty().runOnFXThread { set(value) }
                    }
                    screenShoot("create-new-topic")
                    // Click create button
                    lookupFirst<Button>(CssRule.id("button-create-topic")).click()
                    mainView.lookupFirst<Label>(CssRule.id("topic-$topicName")).text shouldBe topicName
                    screenShoot("list-of-topics-with-new-created-topic")
                }
            }

            "Search topic test" - {
                val searchBox = mainView.lookupFirst<TextField>(CssRule.id("search-box-list-topic").contains(textField))
                "Search for a topic that doesn't exists" {
                    // search for a topic that doesn't exists
                    searchBox.textProperty().runOnFXThread { set("asdffdsaa") }
                    mainView.lookupFirst<ListView<String>>(listView).items.size shouldBe 0
                }

                "Search for $topicName shows only one result" {
                    // search for a topic that doesn't exists
                    searchBox.textProperty().runOnFXThread { set(topicName) }
                    with(mainView.lookupFirst<ListView<String>>(listView)) {
                        items.size shouldBe 1
                        items.first() shouldBe topicName
                    }
                }
            }
        }
    }
})
