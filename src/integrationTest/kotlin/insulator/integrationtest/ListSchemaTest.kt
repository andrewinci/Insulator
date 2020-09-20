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
import org.testfx.util.WaitForAsyncUtils.waitForFxEvents

class ListSchemaTest : FunSpec({

    test("Show the schema list") {
        IntegrationTestContext().use { context ->
            // arrange
            val schemaPrefix = "test-schema"
            val schemas = (1..10).map { "$schemaPrefix$it" }
            schemas.forEach { context.createSchema(it, schemaSample) }
            context.configureDi(
                ConfigurationRepo::class to mockk<ConfigurationRepo>(relaxed = true) {
                    every { getConfiguration() } returns
                        Configuration(clusters = listOf(context.clusterConfiguration)).right()
                }
            )

            // act
            context.startApp(Insulator::class.java)
            // click on the local cluster
            context.clickOn(".cluster")
            // show the menu
            context.clickOn(".icon-button"); waitForFxEvents()
            // select schema registry
            context.clickOn("#menu-item-schema-registry"); waitForFxEvents()

            // assert
            context.lookup<Label> { it.text.startsWith(schemaPrefix) }.queryAll<Label>()
                .map { it.text }.toSet() shouldBe schemas.toSet()
        }
    }
})

private val schemaSample = """
    {
      "type": "record",
      "name": "topLevelRecord",
      "fields": [
        {"name": "a", "type": "int"},
        {"name": "b", "type": "float"}
      ]
    }
""".trimIndent()
