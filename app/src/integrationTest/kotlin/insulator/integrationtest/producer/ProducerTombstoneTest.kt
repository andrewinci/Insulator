package insulator.integrationtest.producer

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
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.freeSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.ints.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldNotBe
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.TableView
import javafx.scene.control.TextInputControl
import kotlinx.coroutines.delay
import tornadofx.CssRule
import tornadofx.Stylesheet
import kotlin.time.ExperimentalTime

@ExperimentalTime
class ProducerTombstoneTest : FreeSpec({
    include(testProduceToCompactedTopic(false))
    include(testProduceToCompactedTopic(true))
})

@ExperimentalTime
fun testProduceToCompactedTopic(testTombstones: Boolean) = freeSpec {
    // customize log
    val withTombstone = if (testTombstones) " with tombstones" else ""

    "Test produce to compacted topic$withTombstone" - {
        IntegrationTestFixture().use { fixture ->
            val numberOfMessages = 12
            val clusterName = "Test cluster"
            fixture.startAppWithKafkaCuster(clusterName, false)

            // create topic
            val testTopicName = "test-producer-topic"
            fixture.createCompactedTopic(testTopicName) shouldBeRight {}

            // open main view
            selectCluster(fixture.currentKafkaCluster)
            val mainView = waitWindowWithTitle("Insulator")

            "Produce to one topic" {

                // select topic
                mainView.selectTopic("topic-$testTopicName")

                // start consumer
                mainView.startStopConsumer()

                repeat(numberOfMessages) { index ->
                    // open producer view
                    mainView.lookupFirst<Button>(CssRule.id("button-produce")).click()
                    val producerView = waitWindowWithTitle("Insulator Producer")

                    // set key and value
                    mapOf("field-producer-key" to index.toString(), "field-producer-value" to "first-value")
                        .map { (id, value) ->
                            producerView
                                .lookupFirst<TextInputControl>(CssRule.id(id))
                                .runOnFXThread { textProperty().set(value) }
                        }

                    producerView.lookupFirst<Button>(CssRule.id("button-producer-send")).click()
                    delay(500)
                }

                screenShoot("produce-messages$withTombstone")

                // send tombstones
                repeat(numberOfMessages) { index ->
                    // open producer view
                    mainView.lookupFirst<Button>(CssRule.id("button-produce")).click()
                    val producerView = waitWindowWithTitle("Insulator Producer")

                    // set key and value
                    mapOf("field-producer-key" to index.toString(), "field-producer-value" to "updated-value-$index")
                        .map { (id, value) ->
                            producerView
                                .lookupFirst<TextInputControl>(CssRule.id(id))
                                .runOnFXThread { textProperty().set(value) }
                        }
                    if (testTombstones) {
                        // the only checkbox is the tombstone one
                        producerView.lookupFirst<CheckBox>(CssRule.c("check-box")).runOnFXThread { isSelected = true }
                    }

                    producerView.lookupFirst<Button>(CssRule.id("button-producer-send")).click()
                    delay(500)
                }

                screenShoot("produce-to-compacted-topics$withTombstone")
                // consume all records again
                mainView.startStopConsumer()
                delay(10_000)
                mainView.startStopConsumer()

                // assert
                val recordTable = mainView.lookupFirst<TableView<RecordViewModel>>(Stylesheet.tableView)
                if (testTombstones) {
                    // at least one record was removed by compacting the tombstone and cleanup
                    recordTable.items.map { it.keyProperty.value }.toSet() shouldNotBe (0 until numberOfMessages)
                    recordTable.items.size shouldBeLessThanOrEqual numberOfMessages
                }
                else {
                    // at least 1 message was updated
                    recordTable.items.size shouldBeLessThan 2*numberOfMessages
                    recordTable.items.map { it.valueProperty.value } shouldContainAll (0 until numberOfMessages).map { "updated-value-$it" }
                }

                // stop consumer
                delay(500)
                mainView.startStopConsumer()
                screenShoot("update-records-$withTombstone")
            }
        }
    }
}
