package insulator.integrationtest

import arrow.core.right
import insulator.Insulator
import insulator.integrationtest.helper.IntegrationTestContext
import insulator.lib.configuration.ConfigurationRepo
import insulator.lib.configuration.model.Configuration
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import javafx.scene.control.Label
import org.testfx.util.WaitForAsyncUtils.waitForFxEvents

class ListTopicTest : FunSpec({

    test("Show the list of topic") {
        IntegrationTestContext(createKafkaCluster = true, createSchemaRegistry = false).use {
            // arrange
            val topicPrefix = "test-topic"
            val topics = (1..10).map { n -> "$topicPrefix$n" }
            topics.forEach { name -> it.createTopics(name) }
            it.configureDi(
                ConfigurationRepo::class to mockk<ConfigurationRepo> {
                    every { addNewClusterCallback(any()) } just runs
                    every { getConfiguration() } returns
                        Configuration(clusters = listOf(it.clusterConfiguration)).right()
                }
            )

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
})
