package insulator.integrationtest

import arrow.core.right
import insulator.Insulator
import insulator.integrationtest.helper.cleanupFXFramework
import insulator.integrationtest.helper.configureFXFramework
import insulator.integrationtest.helper.configureIntegrationDi
import insulator.integrationtest.helper.waitPrimaryStage
import insulator.lib.configuration.ConfigurationRepo
import insulator.lib.configuration.model.Cluster
import insulator.lib.configuration.model.Configuration
import insulator.lib.helpers.runOnFXThread
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import javafx.stage.Stage
import org.testfx.api.FxAssert
import org.testfx.matcher.control.LabeledMatchers
import tornadofx.* // ktlint-disable no-wildcard-imports

class ListClusterTest : FunSpec({

    test("Insulator start successfully showing the list of clusters") {
        // arrange
        val sut = Insulator()
        val cluster = Cluster.empty().copy(name = "Test cluster")
        configureIntegrationDi(
            ConfigurationRepo::class to mockk<ConfigurationRepo> {
                every { addNewClusterCallback(any()) } just runs
                every { getConfiguration() } returns
                    Configuration(clusters = listOf(cluster)).right()
            }
        )

        // act
        sut.runOnFXThread { start(FX.getPrimaryStage()!!) }

        // assert
        val scene = waitPrimaryStage().scene
        (scene.window as Stage).title shouldBe "Insulator"
        FxAssert.verifyThat("#label-cluster-name-${cluster.guid}", LabeledMatchers.hasText(cluster.name))
    }

    beforeTest { configureFXFramework() }
    afterTest { cleanupFXFramework() }
})
