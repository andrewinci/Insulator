package insulator.ui

import arrow.core.right
import insulator.configuration.ConfigurationRepo
import insulator.configuration.model.Configuration
import insulator.configuration.model.InsulatorTheme
import io.kotest.core.spec.style.FreeSpec
import io.mockk.clearStaticMockk
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import javafx.collections.FXCollections
import javafx.stage.Stage
import javafx.stage.Window

class ThemeHelperTest : FreeSpec({

    "theme helper tests" - {
        val configurationRepoMock = mockk<ConfigurationRepo> {
            coEvery { getConfiguration() } returns Configuration(emptyList(), InsulatorTheme.Dark).right()
            coEvery { store(any<InsulatorTheme>()) } returns Unit.right()
        }

        "changeTheme updates the configurations" {
            // arrange
            val sut = ThemeHelper(configurationRepoMock)
            // act
            sut.toggleTheme()
            // assert
            coVerify(exactly = 1) { configurationRepoMock.store(InsulatorTheme.Light) }
        }

        "update theme for the UI" {
            mockkStatic(Window::class)
            val mockWindow = mockk<Stage> {
                every { scene } returns mockk {
                    every { stylesheets } returns FXCollections.observableArrayList("")
                }
            }
            every { Window.getWindows() } returns FXCollections.observableArrayList(listOf(mockWindow))
            // arrange
            val sut = ThemeHelper(configurationRepoMock)
            // act
            sut.updateUITheme()
            // assert
            verify { mockWindow.scene }
            clearStaticMockk(Window::class)
        }
    }
})
