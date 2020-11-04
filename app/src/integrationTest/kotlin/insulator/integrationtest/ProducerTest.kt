package insulator.integrationtest

import insulator.helper.runOnFXThread
import insulator.integrationtest.helpers.IntegrationTestFixture
import insulator.integrationtest.helpers.click
import insulator.integrationtest.helpers.doubleClick
import insulator.integrationtest.helpers.lookupFirst
import insulator.integrationtest.helpers.screenShoot
import insulator.integrationtest.helpers.selectCluster
import insulator.integrationtest.helpers.waitWindowWithTitle
import insulator.viewmodel.main.topic.RecordViewModel
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TableView
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import kotlinx.coroutines.delay
import tornadofx.CssRule
import tornadofx.Stylesheet
import kotlin.time.ExperimentalTime

@ExperimentalTime
class ProducerTest : FreeSpec({

    "Test producer" - {
        IntegrationTestFixture().use { fixture ->
            suspend fun Node.selectTopic(topicName: String) = lookupFirst<Label>(CssRule.id(topicName)).doubleClick()
            suspend fun List<Pair<String, String>>.produce(topicName: String) = forEach { (k, v) -> fixture.stringProducer.send(topicName, k, v) }

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
                mainView.lookupFirst<Button>(CssRule.id("button-consume-stop")).click()

                // open producer view
                mainView.lookupFirst<Button>(CssRule.id("button-produce")).click()
                val producerView = waitWindowWithTitle("Insulator Producer")

                // set key and value
                producerView.lookupFirst<TextField>(CssRule.id("field-producer-key")).runOnFXThread { textProperty().set(testKey) }
                producerView.lookupFirst<TextArea>(CssRule.id("field-producer-value")).runOnFXThread { textProperty().set(testValue) }

                screenShoot("producer-view")

                producerView.lookupFirst<Button>(CssRule.id("button-producer-send")).click()

                // assert
                val recordTable = mainView.lookupFirst<TableView<RecordViewModel>>(Stylesheet.tableView)
                recordTable.items.map { it.keyProperty.value to it.valueProperty.value } shouldContainExactlyInAnyOrder listOf(testKey to testValue)

                mainView.lookupFirst<Button>(CssRule.id("button-consume-stop")).click()
                screenShoot("consume-produced-record")
            }
        }
    }
})
