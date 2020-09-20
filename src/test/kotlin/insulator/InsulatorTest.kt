package insulator

import helper.cleanupFXFramework
import helper.configureFXFramework
import insulator.di.DIContainer
import insulator.lib.helpers.runOnFXThread
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import javafx.collections.FXCollections
import javafx.stage.Stage
import javafx.stage.Window
import tornadofx.* // ktlint-disable no-wildcard-imports

class InsulatorTest : FunSpec({
    test("Set the primary stage onClose handler") {
        // arrange
        val sut = Insulator()

        runOnFXThread {
            // act
            sut.start(FX.primaryStage)
            // assert
            FX.primaryStage.onCloseRequestProperty().value shouldNotBe null
        }
    }

    test("On stop all windows are closed") {
        // arrange
        val sut = Insulator()
        val mockWindows = (1..10).map { mockk<Stage>(relaxed = true) }
        runOnFXThread {
            sut.start(FX.primaryStage)
            mockkStatic(Window::class)
            every { Window.getWindows() } returns FXCollections.observableArrayList(mockWindows)

            // act
            sut.stop()

            // assert
            verify(exactly = 1) { Window.getWindows() }
            mockWindows.forEach {
                verify(exactly = 1) { it.close() }
            }
        }
    }

    beforeTest {
        FX.dicontainer = DIContainer()
        configureFXFramework()
    }
    afterTest { cleanupFXFramework() }
})
