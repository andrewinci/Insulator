package insulator.viewmodel.configurations

import insulator.configuration.model.InsulatorTheme
import insulator.helper.GlobalState
import insulator.helper.dispatch
import insulator.ui.ThemeHelper
import javafx.beans.property.SimpleBooleanProperty
import tornadofx.onChange
import javax.inject.Inject

class ConfigurationViewModel @Inject constructor(private val themeHelper: ThemeHelper) {
    val darkTheme = SimpleBooleanProperty().apply {
        onChange {
            themeHelper.dispatch {
                if (it) setTheme(InsulatorTheme.Dark)
                else setTheme(InsulatorTheme.Light)
            }
        }
        themeHelper.dispatch { currentTheme().map { set(it == InsulatorTheme.Dark) } }
    }
    val readonlyMode = GlobalState.isReadOnlyProperty
    val humanReadableAvro = GlobalState.humanReadableAvroProperty
}
