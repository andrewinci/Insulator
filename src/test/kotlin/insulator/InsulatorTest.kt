package insulator

import helper.cleanupFXFramework
import helper.configureFXFramework
import insulator.di.DIContainer
import insulator.lib.helpers.runOnFXThread
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldNotBe
import tornadofx.* // ktlint-disable no-wildcard-imports

class InsulatorTest : FunSpec({
    test("Stop is called when the primary stage is closed") {
        // arrange
        val sut = Insulator()

        runOnFXThread {
            // act
            sut.start(FX.primaryStage)
            // assert
            FX.primaryStage.onCloseRequestProperty().value shouldNotBe null
        }
    }

    beforeTest {
        FX.dicontainer = DIContainer()
        configureFXFramework()
    }
    afterTest { cleanupFXFramework() }
})
