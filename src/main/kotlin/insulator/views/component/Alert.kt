package insulator.views.component

import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import tornadofx.FX

fun insulatorAlert(level: Alert.AlertType, message: String, vararg buttons: ButtonType, fn: ((ButtonType) -> Unit)? = null) {
    val alert = Alert(level, "", *buttons)
    alert.headerText = message
    alert.dialogPane.stylesheets.addAll(FX.stylesheets)
    val buttonClicked = alert.showAndWait()
    if (buttonClicked.isPresent) {
        fn?.invoke(buttonClicked.get())
    }
}