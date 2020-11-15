package insulator.integrationtest

import insulator.integrationtest.helpers.IntegrationTestFixture
import insulator.integrationtest.helpers.click
import insulator.integrationtest.helpers.lookupFirst
import insulator.integrationtest.helpers.screenShoot
import insulator.integrationtest.helpers.selectCluster
import insulator.integrationtest.helpers.waitWindowWithTitle
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ListView
import kotlinx.coroutines.delay
import tornadofx.CssRule
import tornadofx.Stylesheet.Companion.listView
import kotlin.time.ExperimentalTime

@ExperimentalTime
class RefreshButtonTests : FreeSpec({

    "Test refresh topic button" - {
        IntegrationTestFixture().use { fixture ->
            fixture.startAppWithKafkaCuster("Test cluster")
            selectCluster(fixture.currentKafkaCluster)

            val mainView = waitWindowWithTitle("Insulator")

            "Refresh topic list" {
                val topicName = "test-new-topic"
                // create topic
                fixture.createTopic(topicName)
                delay(1_000)
                // the topic shouldn't be visible
                mainView.lookupFirst<ListView<String>>(listView).items.count { it == topicName } shouldBe 0
                // click the refresh button wil load the new topic
                mainView.lookupFirst<Button>(CssRule.id("button-refresh")).click()
                screenShoot("refresh-topic-list")
                // the list of topic is now updated
                mainView.lookupFirst<ListView<String>>(listView).items.count { it == topicName } shouldBe 1
                mainView.lookupFirst<Label>(CssRule.id("topic-$topicName")).text shouldBe topicName
            }

//            "Refresh schema list" {
//                val schemaName = "test-new-topic-schema"
//                // select schema registry
//                mainView.lookupFirst<Node>(CssRule.id("sidebar-item-schema-registry")).click()
//                // create the schema
//                fixture.createTestSchema(schemaName)
//            // the topic shouldn't be visible
//            mainView.lookupFirst<ListView<String>>(listView).items.count { it == topicName } shouldBe 0
//            // click the refresh button wil load the new topic
//            mainView.lookupFirst<Button>(CssRule.id("button-refresh")).click()
//            screenShoot("refresh-topic-list")
//            // the list of topic is now updated
//            mainView.lookupFirst<ListView<String>>(listView).items.count { it == topicName } shouldBe 1
//            mainView.lookupFirst<Label>(CssRule.id("topic-$topicName")).text shouldBe topicName
//            }
        }
    }
})
