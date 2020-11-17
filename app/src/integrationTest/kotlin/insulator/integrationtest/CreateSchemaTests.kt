package insulator.integrationtest

import insulator.helper.runOnFXThread
import insulator.integrationtest.helpers.IntegrationTestFixture
import insulator.integrationtest.helpers.click
import insulator.integrationtest.helpers.lookupFirst
import insulator.integrationtest.helpers.screenShoot
import insulator.integrationtest.helpers.selectCluster
import insulator.integrationtest.helpers.waitWindowWithTitle
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import kotlinx.coroutines.delay
import tornadofx.CssRule
import tornadofx.Stylesheet.Companion.listView
import tornadofx.Stylesheet.Companion.textArea
import tornadofx.Stylesheet.Companion.textField
import kotlin.time.ExperimentalTime

@ExperimentalTime
class CreateSchemaTests : FreeSpec({

    "Test create a new schema" - {
        IntegrationTestFixture().use { fixture ->
            fixture.startAppWithKafkaCuster("Test cluster")
            selectCluster(fixture.currentKafkaCluster)

            val mainView = waitWindowWithTitle("Insulator")
            suspend fun getListViewItems() = mainView.lookupFirst<ListView<String>>(listView).items

            "Create a new schema" {
                val schemaName = "test-new-topic-schema"
                // select schema registry
                mainView.lookupFirst<Node>(CssRule.id("sidebar-item-schema-registry")).click()
                // click create the schema
                mainView.lookupFirst<Button>(CssRule.id("button-create-schema")).click()
                delay(1_000)
                val createSchemaView = waitWindowWithTitle("New subject")
                // set the schema name
                createSchemaView.lookupFirst<TextField>(textField).runOnFXThread { text = schemaName }
                // set the schema value
                createSchemaView.lookupFirst<TextArea>(textArea).runOnFXThread { text = fixture.testSchema() }
                delay(500)
                screenShoot("create-new-schema")

                // save
                createSchemaView.lookupFirst<Button>(CssRule.id("button-schema-register")).click()

                // click the refresh button will load the new schema
                mainView.lookupFirst<Button>(CssRule.id("button-refresh-schema-list")).click()

                // the list of topic is now updated
                getListViewItems().count { it == schemaName } shouldBe 1
                mainView.lookupFirst<Label>(CssRule.id("schema-$schemaName")).text shouldBe schemaName
            }
        }
    }
})
