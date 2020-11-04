package insulator.ui

import insulator.configuration.ConfigurationRepo
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

    suspend fun changeTheme() {
        configurationRepo.getConfiguration().map {
            configurationRepo.store(
                if (it.theme == InsulatorTheme.Dark) InsulatorTheme.Light else InsulatorTheme.Dark
            )
        }
        updateUITheme()
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
