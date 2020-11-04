package insulator.ui.style

import insulator.configuration.ConfigurationRepo
import insulator.configuration.model.InsulatorTheme
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.stage.Stage
import javafx.stage.Window
import tornadofx.Dimension
import tornadofx.Stylesheet
import tornadofx.box
import tornadofx.c
import tornadofx.em
import tornadofx.multi
import tornadofx.px
import tornadofx.reloadStylesheets
import javax.inject.Inject
import javax.inject.Singleton

class Root : Stylesheet() {
    init {
        root {
            font = Font.font("Helvetica", 10.0)
            backgroundColor = multi(theme.backgroundColor)
            padding = box(theme.viewPadding)
            contextMenu {
                padding = box(0.px)
                minWidth = 100.0.px
                textFill = theme.black
                menuItem {
                    padding = box(10.0.px)
                    and(focused) {
                        backgroundColor = multi(theme.mainColor)
                        label {
                            textFill = theme.backgroundColor
                            textFill = theme.backgroundColor
                        }
                    }
                }
            }
        }
    }
}

data class Theme(
    val black: Color = Color.BLACK,
    val backgroundColor: Color = Color.WHITE,
    val mainColor: Color = c("#FF9100"),
    val mainColorDark: Color = c("#D65400"),
    val alertColor: Color = c("#cc0016"),
    val alertColorDark: Color = c("#960017"),
    val lightGray: Color = c("#ccc"),
    val darkGray: Color = c("#666"),
    val blueColor: Color = c("#20a3f5"),
    val greenColor: Color = c("#23cc3f"),
    val viewPadding: Dimension<Dimension.LinearUnits> = 1.em,
)

private val darkTheme = Theme(
    black = Color.WHITE,
    backgroundColor = c("#292b2e"),
    mainColor = c("#FF9100"),
    mainColorDark = c("#D65400"),
    alertColor = c("#cc0016"),
    alertColorDark = c("#960017"),
    lightGray = c("#666"),
    darkGray = c("#ccc"),
    blueColor = c("#20a3f5"),
    viewPadding = 1.em,
)
private val lightTheme = Theme()

var theme: Theme = lightTheme
    private set

@Singleton
class ThemeHelper @Inject constructor(val configurationRepo: ConfigurationRepo) {

    suspend fun changeTheme() {
        configurationRepo.getConfiguration().map {
            configurationRepo.store(
                if (it.theme == InsulatorTheme.Dark) InsulatorTheme.Light else InsulatorTheme.Dark
            )
        }
        updateUITheme()
    }

    private fun reloadTheme() =
        Window.getWindows().map { it as? Stage }
            .forEach { it?.scene?.reloadStylesheets() }

    suspend fun updateUITheme() {
        configurationRepo.getConfiguration().map {
            theme = when (it.theme) {
                InsulatorTheme.Dark -> darkTheme
                InsulatorTheme.Light -> lightTheme
            }
            reloadTheme()
        }
    }
}
