package insulator.integrationtest

import insulator.helper.runOnFXThread
import insulator.integrationtest.helpers.IntegrationTestFixture
import insulator.integrationtest.helpers.click
import insulator.integrationtest.helpers.lookupFirst
import insulator.integrationtest.helpers.screenShoot
import insulator.integrationtest.helpers.selectCluster
import insulator.integrationtest.helpers.selectTopic
import insulator.integrationtest.helpers.startStopConsumer
import insulator.integrationtest.helpers.waitWindowWithTitle
import insulator.viewmodel.main.topic.RecordViewModel
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import javafx.scene.control.Button
import javafx.scene.control.TableView
import javafx.scene.control.TextInputControl
import tornadofx.CssRule
import tornadofx.Stylesheet
import kotlin.time.ExperimentalTime

@ExperimentalTime
class ProducerTest : FreeSpec({

    "Test producer" - {
        IntegrationTestFixture().use { fixture ->

            val clusterName = "Test cluster"
            fixture.startAppWithKafkaCuster(clusterName, false)

            // create topic
            val testTopicName = "test-topic"
            fixture.createTopic(testTopicName)

            // open main view
            selectCluster(fixture.currentKafkaCluster)
            val mainView = waitWindowWithTitle("Insulator")

            "Produce to one topic" {
                val testKey = "test-key"
                val testValue = "test producer value"

                // select topic
                mainView.selectTopic("topic-$testTopicName")

                // start consumer
                mainView.startStopConsumer()

                // open producer view
                mainView.lookupFirst<Button>(CssRule.id("button-produce")).click()
                val producerView = waitWindowWithTitle("Insulator Producer")

                // set key and value
                mapOf("field-producer-key" to testKey, "field-producer-value" to testValue)
                    .map { (id, value) ->
                        producerView
                            .lookupFirst<TextInputControl>(CssRule.id(id))
                            .runOnFXThread { textProperty().set(value) }
                    }

                screenShoot("producer-view")

                producerView.lookupFirst<Button>(CssRule.id("button-producer-send")).click()

                // assert
                val recordTable = mainView.lookupFirst<TableView<RecordViewModel>>(Stylesheet.tableView)
                recordTable.items.map { it.keyProperty.value to it.valueProperty.value } shouldContainExactlyInAnyOrder listOf(testKey to testValue)

                mainView.startStopConsumer()
                screenShoot("consume-produced-record")
            }
        }
    }
})
