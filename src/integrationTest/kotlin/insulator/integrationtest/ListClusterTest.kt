package insulator.integrationtest

import arrow.core.right
import insulator.Insulator
import insulator.integrationtest.helper.IntegrationTestContext
import insulator.lib.configuration.ConfigurationRepo
import insulator.lib.configuration.model.Cluster
import insulator.lib.configuration.model.Configuration
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import javafx.stage.Stage
import org.testfx.api.FxAssert
import org.testfx.matcher.control.LabeledMatchers
import tornadofx.FX

class ListClusterTest : StringSpec({

    "Insulator start successfully showing the list of clusters" {
        IntegrationTestContext(false).use {
            // arrange
            val cluster = Cluster.empty().copy(name = "Test cluster")
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
            FxAssert.verifyThat("#cluster-${cluster.guid} .label", LabeledMatchers.hasText(cluster.name))
        }
    }
})
