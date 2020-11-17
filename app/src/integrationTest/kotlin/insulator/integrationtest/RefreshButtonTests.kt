package insulator.integrationtest

import insulator.integrationtest.helpers.IntegrationTestFixture
import insulator.integrationtest.helpers.click
import insulator.integrationtest.helpers.doubleClick
import insulator.integrationtest.helpers.lookupFirst
import insulator.integrationtest.helpers.screenShoot
import insulator.integrationtest.helpers.selectCluster
import insulator.integrationtest.helpers.waitWindowWithTitle
import insulator.kafka.model.Schema
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.ListView
import kotlinx.coroutines.delay
import tornadofx.CssRule
import tornadofx.Stylesheet.Companion.listView
import tornadofx.Stylesheet.Companion.text
import tornadofx.selectedItem
import kotlin.time.ExperimentalTime

@ExperimentalTime
class RefreshButtonTests : FreeSpec({

    "Test refresh topic button" - {
        IntegrationTestFixture().use { fixture ->
            fixture.startAppWithKafkaCuster("Test cluster")
            selectCluster(fixture.currentKafkaCluster)

            val mainView = waitWindowWithTitle("Insulator")
            suspend fun getListViewItems() = mainView.lookupFirst<ListView<String>>(listView).items

            "Refresh topic list" {
                val topicName = "test-new-topic"
                // create topic
                fixture.createTopic(topicName)
                delay(1_000)
                // the topic shouldn't be visible
                getListViewItems().count { it == topicName } shouldBe 0
                // click the refresh button wil load the new topic
                mainView.lookupFirst<Button>(CssRule.id("button-refresh-topic-list")).click()
                screenShoot("refresh-topic-list")
                // the list of topic is now updated
                getListViewItems().count { it == topicName } shouldBe 1
                mainView.lookupFirst<Label>(CssRule.id("topic-$topicName")).text shouldBe topicName
            }

            "Refresh schema list" {
                val schemaName = "test-new-topic-schema"
                // select schema registry
                mainView.lookupFirst<Node>(CssRule.id("sidebar-item-schema-registry")).click()
                // create the schema
                fixture.createTestSchema(schemaName)
                delay(1_000)
                // the schema shouldn't be visible
                getListViewItems().count { it == schemaName } shouldBe 0
                // click the refresh button wil load the new schema
                mainView.lookupFirst<Button>(CssRule.id("button-refresh-schema-list")).click()
                screenShoot("refresh-schema-registry-list")
                // the list of topic is now updated
                getListViewItems().count { it == schemaName } shouldBe 1
                mainView.lookupFirst<Label>(CssRule.id("schema-$schemaName")).text shouldBe schemaName
            }

            "Refresh single schema" {
                val schemaName = "test-new-topic-schema-2"
                // select schema registry
                mainView.lookupFirst<Node>(CssRule.id("sidebar-item-schema-registry")).click()
                // create the schema
                fixture.createTestSchema(schemaName)
                // click the schema list refresh button will load the new schema
                mainView.lookupFirst<Button>(CssRule.id("button-refresh-schema-list")).click()
                // select the test schema
                mainView.lookupFirst<Label>(CssRule.id("schema-$schemaName")).doubleClick()
                delay(500)
                with(mainView.lookupFirst<ComboBox<Schema>>(CssRule.id("combobox-schema-version"))) {
                    selectedItem?.version shouldBe 1
                    items.size shouldBe 1
                }

                // update the schema
                fixture.createTestSchemaUpdate(schemaName)
                delay(1_000)
                // click the refresh button will load the new schema
                mainView.lookupFirst<Button>(CssRule.id("button-refresh-schema")).click()
                with(mainView.lookupFirst<ComboBox<Schema>>(CssRule.id("combobox-schema-version"))) {
                    selectedItem?.version shouldBe 2
                    items.size shouldBe 2
                }
            }
        }
    }
})
