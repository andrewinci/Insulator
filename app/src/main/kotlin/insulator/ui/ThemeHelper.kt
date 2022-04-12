package insulator.ui

import arrow.core.Either
import insulator.configuration.ConfigurationRepo
import insulator.configuration.ConfigurationRepoException
import insulator.configuration.model.InsulatorTheme
import insulator.ui.style.darkTheme
import insulator.ui.style.lightTheme
import insulator.ui.style.theme
import javafx.stage.Stage
import javafx.stage.Window
import tornadofx.reloadStylesheets
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeHelper @Inject constructor(private val configurationRepo: ConfigurationRepo) {

    suspend fun setTheme(theme: InsulatorTheme) {
        configurationRepo.getConfiguration().map {
            configurationRepo.store(theme)
        }
        updateUITheme()
    }

    suspend fun currentTheme(): Either<ConfigurationRepoException, InsulatorTheme> {
        return configurationRepo.getConfiguration().map { it.theme }
    }

    suspend fun toggleTheme() {
        configurationRepo.getConfiguration().map {
            if (it.theme == InsulatorTheme.Dark) setTheme(InsulatorTheme.Light) else setTheme(InsulatorTheme.Dark)
        }
    }

    suspend fun updateUITheme() {
        configurationRepo.getConfiguration().map {
            theme = when (it.theme) {
                InsulatorTheme.Dark -> darkTheme
                InsulatorTheme.Light -> lightTheme
            }
            reloadTheme()
        }
    }

    private fun reloadTheme() =
        Window.getWindows().map { it as? Stage }
            .forEach { it?.scene?.reloadStylesheets() }
}
