package insulator

import helper.cleanupFXFramework
import helper.configureFXFramework
import insulator.di.DIContainer
import insulator.lib.helpers.runOnFXThread
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import javafx.stage.Stage
import tornadofx.* // ktlint-disable no-wildcard-imports
import java.lang.Thread.sleep

class InsulatorTest : FunSpec({

    test("Insulator start successfully") {
        // arrange
        val sut = Insulator()
        FX.dicontainer = DIContainer()
        // act
        configureFXFramework()
        sut.runOnFXThread { start(FX.getPrimaryStage()!!) }
        // wait for primary stage to be visible
        while (FX.getPrimaryStage()?.isShowing != true) sleep(200)
        // assert
        val primaryStage = FX.getPrimaryStage()!!
        primaryStage.isShowing shouldBe true
        (primaryStage.scene.window as Stage).title shouldBe "Insulator"
    }

    afterTest { cleanupFXFramework() }
})
