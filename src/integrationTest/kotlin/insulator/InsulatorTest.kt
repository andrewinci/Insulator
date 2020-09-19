package insulator

import helper.cleanupFXFramework
import helper.configureFXFramework
import insulator.di.DIContainer
import insulator.lib.helpers.runOnFXThread
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import tornadofx.* // ktlint-disable no-wildcard-imports
import java.lang.Thread.sleep

class InsulatorTest : FunSpec({

    test("Happy path") {
        // arrange
        val sut = Insulator()
        FX.dicontainer = DIContainer()
        // act
        configureFXFramework()
        sut.runOnFXThread { start(FX.getPrimaryStage()!!) }
        // wait for primary stage to be visible
        while (FX.getPrimaryStage()?.isShowing != true) sleep(200)
        FX.getPrimaryStage()?.isShowing shouldBe true
    }

    afterTest { cleanupFXFramework() }
})
