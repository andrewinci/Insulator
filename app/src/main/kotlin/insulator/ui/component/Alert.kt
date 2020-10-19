package insulator.ui.component

import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import tornadofx.FX

fun insulatorAlert(level: Alert.AlertType, message: String, vararg buttons: ButtonType, fn: ((ButtonType) -> Unit)? = null) {
    val buttonClicked = Alert(level, "", *buttons).apply {
        headerText = message
        dialogPane.stylesheets.addAll(FX.stylesheets)
    }.showAndWait()
    if (buttonClicked.isPresent) fn?.invoke(buttonClicked.get())
}
