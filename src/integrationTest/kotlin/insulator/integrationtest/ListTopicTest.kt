package insulator.integrationtest

import insulator.Insulator
import insulator.integrationtest.helper.IntegrationTestContext
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.stage.Window
import org.testfx.util.WaitForAsyncUtils.waitForFxEvents

class ListTopicTest : FunSpec({

    test("Show the list of topic") {
        IntegrationTestContext(createKafkaCluster = true, createSchemaRegistry = false).use {
            // arrange
            val topicPrefix = "test-topic"
            val topics = (1..10).map { n -> "$topicPrefix$n" }
            topics.forEach { name -> it.createTopics(name) }
            it.configureDi(it.clusterConfiguration)

            // act
            it.startApp(Insulator::class.java)
            // click on the local cluster to show the list of topics
            it.clickOn(".cluster")
            waitForFxEvents()

            // assert
            it.lookup<Label> { label -> label.text.startsWith(topicPrefix) }.queryAll<Label>()
                .map { label -> label.text }.toSet() shouldBe topics.toSet()
        }
    }

    test("Create a new topic update the list of topic") {
        IntegrationTestContext(createKafkaCluster = true, createSchemaRegistry = false).use {
            // arrange
            val topicName = "test-new-topic"
            it.configureDi(it.clusterConfiguration)

            // act
            it.startApp(Insulator::class.java)
            // click on the local cluster to show the list of topics
            it.clickOn(".cluster"); waitForFxEvents()
            // click on create new topic button
            it.clickOn("#button-create-topic"); waitForFxEvents()
            // set name and other fields
            val textFields = it.lookup(".form .text-field").queryAll<TextField>().iterator()
            // click topic name text-field
            it.clickOn(textFields.next()); waitForFxEvents()
            // set the name of the new topic
            it.write(topicName); waitForFxEvents()
            // click number of partitions text-field
            it.clickOn(textFields.next()); waitForFxEvents()
            // set the number of partitions text-field
            it.write("1"); waitForFxEvents()
            // click replication factor text-field
            it.clickOn(textFields.next()); waitForFxEvents()
            // set the replication factor  text-field
            it.write("1"); waitForFxEvents()

            // click create
            it.clickOn(
                it.lookup<Button> { btn -> btn.text == "Create" }.queryButton()
            ); waitForFxEvents()

            // assert
            Window.getWindows().size shouldBe 1
            it.lookup("#topic-$topicName").queryLabeled().text shouldBe topicName
        }
    }

    test("Delete a topic update the list of topic") {
        IntegrationTestContext(createKafkaCluster = true, createSchemaRegistry = false).use {
            // arrange
            val topicName = "test-new-topic"
            val topicId = "#topic-$topicName"
            it.configureDi(it.clusterConfiguration)
            it.createTopics(topicName)

            // act
            it.startApp(Insulator::class.java)
            // click on the local cluster to show the list of topics
            it.clickOn(".cluster"); waitForFxEvents()
            // click on the test topic
            it.doubleClickOn(topicId); waitForFxEvents()
            // click on delete topic
            val deleteButton = it.lookup(".alert-button").lookup<Button> { btn -> btn.text == "delete" }.queryButton()
            it.clickOn(deleteButton); waitForFxEvents()
            // click ok on warning
            it.clickOkOnDialog(); waitForFxEvents()

            // assert
            Window.getWindows().size shouldBe 1
            it.lookup(topicId).queryAll<Label>().size shouldBe 0
        }
    }
})
