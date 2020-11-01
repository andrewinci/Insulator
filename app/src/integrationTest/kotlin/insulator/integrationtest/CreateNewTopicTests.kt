package insulator.integrationtest

import insulator.helper.runOnFXThread
import insulator.integrationtest.helpers.FxFixture
import insulator.integrationtest.helpers.click
import insulator.integrationtest.helpers.eventually
import insulator.integrationtest.helpers.lookupFirst
import insulator.integrationtest.helpers.screenShoot
import insulator.integrationtest.helpers.waitWindowWithTitle
import insulator.ui.style.MainViewStyle
import insulator.ui.style.TextStyle.Companion.h1
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import tornadofx.CssRule
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class CreateNewTopicTests : FreeSpec({

    "Create a new topic tests" - {
        FxFixture().use { fixture ->
            val clusterName = "Test cluster"
            fixture.startAppWithKafkaCuster(clusterName)

            // Wait for the main view to show up
            val mainView = waitWindowWithTitle("Insulator")
            screenShoot("local-cluster-main-view")

            "Cluster name is shown in the sidebar" {
                eventually {
                    mainView.lookupFirst<Label>(MainViewStyle.sidebar.contains(h1)).text shouldBe clusterName
                }
            }

            "Create new topic" {
                val newTopicName = "test_topic_name"
                // Click create topic button
                mainView.lookupFirst<Button>(CssRule.id("button-create-topic")).click()
                // Set topic name and other fields
                with(waitWindowWithTitle("Create new topic")) {
                    mapOf(
                        "field-create-topic-name" to newTopicName,
                        "field-create-topic-number-of-partitions" to "3",
                        "field-create-topic-replication-factor" to "1"
                    ).map { (fieldId, value) ->
                        lookupFirst<TextField>(CssRule.id(fieldId)).runOnFXThread { text = value }
                    }
                    screenShoot("create-new-topic")
                    // Click create button
                    lookupFirst<Button>(CssRule.id("button-create-topic")).click()
                    eventually {
                        mainView.lookupFirst<Label>("topic-$newTopicName").text shouldBe newTopicName
                    }
                }
            }
        }
    }
})
