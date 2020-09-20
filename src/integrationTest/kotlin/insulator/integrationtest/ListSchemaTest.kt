package insulator.integrationtest

import arrow.core.right
import insulator.Insulator
import insulator.integrationtest.helper.IntegrationTestContext
import insulator.lib.configuration.ConfigurationRepo
import insulator.lib.configuration.model.Configuration
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import javafx.scene.control.Label
import javafx.stage.Window
import org.testfx.util.WaitForAsyncUtils.waitForFxEvents

class ListSchemaTest : FunSpec({

    test("Show the schema list") {
        IntegrationTestContext().use {
            // arrange
            val schemaPrefix = "test-schema"
            val schemas = (1..10).map { n -> "$schemaPrefix$n" }
            schemas.forEach { name -> it.createSchema(name, schemaSample) }
            it.configureDi(
                ConfigurationRepo::class to mockk<ConfigurationRepo>(relaxed = true) {
                    every { getConfiguration() } returns
                        Configuration(clusters = listOf(it.clusterConfiguration)).right()
                }
            )

            // act
            it.startApp(Insulator::class.java)
            // click on the local cluster
            it.clickOn(".cluster")
            // show the menu
            it.clickOn(".icon-button"); waitForFxEvents()
            // select schema registry
            it.clickOn("#menu-item-schema-registry"); waitForFxEvents()

            // assert
            it.lookup<Label> { label -> label.text.startsWith(schemaPrefix) }.queryAll<Label>()
                .map { label -> label.text }.toSet() shouldBe schemas.toSet()
        }
    }

    test("Double click on a schema will show it") {
        IntegrationTestContext().use {
            // arrange
            val schemaPrefix = "test-schema"
            val schemas = (1..10).map { "$schemaPrefix-$it" }
            schemas.forEach { name -> it.createSchema(name, schemaSample) }
            it.configureDi(
                ConfigurationRepo::class to mockk<ConfigurationRepo>(relaxed = true) {
                    every { getConfiguration() } returns
                        Configuration(clusters = listOf(it.clusterConfiguration)).right()
                }
            )

            // act
            it.startApp(Insulator::class.java)
            // click on the local cluster
            it.clickOn(".cluster")
            // show the menu
            it.clickOn(".icon-button"); waitForFxEvents()
            // select schema registry
            it.clickOn("#menu-item-schema-registry"); waitForFxEvents()
            it.sleep(2000) // delay due CI
            // open the first schema
            it.doubleClickOn("#schema-$schemaPrefix-1"); waitForFxEvents()

            // assert
            it.sleep(2000) // delay due CI
            Window.getWindows().size shouldBe 2
        }
    }
})

private val schemaSample =
    """{
      "type": "record",
      "name": "topLevelRecord",
      "fields": [
        {"name": "a", "type": "int"},
        {"name": "b", "type": "float"}
      ]
    }
    """.trimIndent()
