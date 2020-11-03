package insulator.integrationtest

import insulator.integrationtest.helpers.IntegrationTestFixture
import insulator.integrationtest.helpers.click
import insulator.integrationtest.helpers.doubleClick
import insulator.integrationtest.helpers.lookupAny
import insulator.integrationtest.helpers.lookupFirst
import insulator.integrationtest.helpers.screenShoot
import insulator.integrationtest.helpers.selectCluster
import insulator.integrationtest.helpers.waitWindowWithTitle
import insulator.viewmodel.main.topic.RecordViewModel
import io.kotest.core.spec.style.FreeSpec
import io.kotest.inspectors.forAtLeastOne
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TableView
import kotlinx.coroutines.delay
import tornadofx.CssRule
import tornadofx.Stylesheet.Companion.tableView
import kotlin.time.ExperimentalTime

@ExperimentalTime
class ConsumerTests : FreeSpec({

    "Test consumers" - {
        IntegrationTestFixture().use { fixture ->
            suspend fun Node.selectTopic(topicName: String) = lookupFirst<Label>(CssRule.id(topicName)).doubleClick()
            suspend fun List<Pair<String, String>>.produce(topicName: String) = forEach { (k, v) -> fixture.stringProducer.send(topicName, k, v) }

            val clusterName = "Test cluster"
            fixture.startAppWithKafkaCuster(clusterName, false)

            // create topics
            val testTopicName = "test-topic"
            (1..3).forEach { fixture.createTopic("$testTopicName-$it") }

            // open main view
            selectCluster(fixture.currentKafkaCluster)
            val mainView = waitWindowWithTitle("Insulator")

            "Consume from one topic" {
                val testTopic1 = "$testTopicName-1"
                mainView.selectTopic("topic-$testTopic1")
                // start consuming
                mainView.lookupFirst<Button>(CssRule.id("button-consume-stop")).click()
                val records = (1..10).map { "key$it" to "value$it" }.also { it.produce(testTopic1) }
                delay(2_000)
                screenShoot("consumer")
                // assert
                val recordTable = mainView.lookupFirst<TableView<RecordViewModel>>(tableView)
                recordTable.items.map { it.keyProperty.value to it.valueProperty.value } shouldContainExactlyInAnyOrder records

                // stop consuming
                mainView.lookupFirst<Button>(CssRule.id("button-consume-stop")).click()
            }

            "Consume from multiple topics" {
                val recordSets = (2..3).map { topicIndex ->
                    val topic = "$testTopicName-$topicIndex"
                    // start consuming from topic it
                    mainView.selectTopic("topic-$topic")
                    mainView.lookupFirst<Button>(CssRule.id("button-consume-stop")).click()

                    // produce to topic it
                    (1..10).map { n -> "key$n" to "$topic-value-$n" }.also { it.produce(topic) }
                }
                delay(4_000)
                screenShoot("multiple-consumers")
                // assert
                with(mainView.lookupAny<TableView<RecordViewModel>>(tableView)) {
                    (0..1).forEach { n ->
                        forAtLeastOne { it.items.map { r -> r.keyProperty.value to r.valueProperty.value } shouldContainExactlyInAnyOrder recordSets[n] }
                    }
                }

                // stop consuming
                mainView.lookupFirst<Button>(CssRule.id("button-consume-stop")).click()
            }
        }
    }
})
