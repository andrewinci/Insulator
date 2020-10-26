package insulator.integrationtest

import insulator.Insulator
import insulator.integrationtest.helpers.FxFixture
import insulator.kafka.model.Cluster
import io.kotest.assertions.timing.eventually
import io.kotest.core.spec.style.StringSpec
import kotlinx.coroutines.delay
import org.testfx.api.FxAssert
import org.testfx.api.FxToolkit
import org.testfx.matcher.control.LabeledMatchers
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

@ExperimentalTime
class HappyPath : StringSpec({
    "Happy path start the app and show list clusters view" {
        FxFixture().use {
            // arrange
            it.storeConfiguration(Cluster(name = "test cluster", endpoint = "endpoint"))
            // act
            FxToolkit.setupApplication(Insulator::class.java)
            delay(60_000)
            // assert
            it.waitFXThread()
            eventually(10.seconds) {
                FxAssert.verifyThat(".label", LabeledMatchers.hasText("Clusters"))
            }
        }
    }
})
