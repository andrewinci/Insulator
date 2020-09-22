package insulator.integrationtest

import arrow.core.right
import insulator.Insulator
import insulator.integrationtest.helper.IntegrationTestContext
import insulator.lib.configuration.ConfigurationRepo
import insulator.lib.configuration.model.Cluster
import insulator.lib.configuration.model.Configuration
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import javafx.scene.Node
import javafx.scene.control.TextField
import javafx.stage.Stage
import javafx.stage.Window
import org.testfx.api.FxAssert
import org.testfx.matcher.control.LabeledMatchers
import org.testfx.util.WaitForAsyncUtils.waitForFxEvents
import tornadofx.FX

class ListClusterTest : FunSpec({

    test("Insulator start successfully showing the list of clusters") {
        IntegrationTestContext(false).use {
            // arrange
            val cluster = Cluster.empty().copy(name = "Test cluster", endpoint = "endpoint")
            it.configureDi(
                ConfigurationRepo::class to mockk<ConfigurationRepo> {
                    every { addNewClusterCallback(any()) } just runs
                    every { getConfiguration() } returns
                        Configuration(clusters = listOf(cluster)).right()
                }
            )

            // act
            it.startApp(Insulator::class.java)

            // assert
            (FX.primaryStage.scene.window as Stage).title shouldBe "Insulator"
            FxAssert.verifyThat(".cluster .h2", LabeledMatchers.hasText(cluster.name))
            FxAssert.verifyThat(".cluster .h3", LabeledMatchers.hasText(cluster.endpoint))
        }
    }

    test("Create a new cluster update the list in the main view") {
        IntegrationTestContext(false).use {
            // arrange
            val clusterName = "New cluster name"
            val endpoint = "https://endpoint:9090"
            it.configureDi()
            // act
            it.startApp(Insulator::class.java)
            // click "Create new cluster"
            it.clickOn("#button-create-cluster"); waitForFxEvents()
            val textFields = it.lookup(".text-field").queryAll<TextField>().iterator()
            // click cluster name text-field
            it.clickOn(textFields.next())
            // set the name of the new cluster
            it.write(clusterName); waitForFxEvents()
            // click endpoint  text-field
            it.clickOn(textFields.next()); waitForFxEvents()
            // write the endpoint
            it.write(endpoint); waitForFxEvents()
            // save
            it.clickOn("#button-save-cluster"); waitForFxEvents()

            // assert
            Window.getWindows().size shouldBe 1
            FxAssert.verifyThat(".cluster .h2", LabeledMatchers.hasText(clusterName))
            FxAssert.verifyThat(".cluster .h3", LabeledMatchers.hasText(endpoint))
        }
    }

    test("Delete a cluster update the list in the main view") {
        IntegrationTestContext(false).use {
            // arrange
            it.configureDi()
            it.getInstance(ConfigurationRepo::class).store(cluster = Cluster.empty().copy(name = "DeleteMe"))

            // act
            it.startApp(Insulator::class.java)
            // click on the cluster settings
            it.clickOn(".cluster .icon-button"); waitForFxEvents()
            // click on the delete button
            it.clickOn(".alert-button"); waitForFxEvents()
            // press Ok on the warning dialog
            it.clickOkOnDialog(); waitForFxEvents()

            // assert
            it.lookup(".cluster .h2").tryQuery<Node>().isEmpty shouldBe true
        }
    }
})
