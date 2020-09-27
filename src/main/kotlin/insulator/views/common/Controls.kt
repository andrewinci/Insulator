package insulator.views.common

import insulator.styles.Controls
import javafx.beans.property.Property
import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import tornadofx.* // ktlint-disable no-wildcard-imports

fun EventTarget.searchBox(searchText: Property<String>) =
    hbox {
        label("Search")
        textfield(searchText) { minWidth = 200.0 }.also { it.parent.focusedProperty().addListener(ChangeListener { _, _, _ -> it.requestFocus() }) }
        alignment = Pos.CENTER_RIGHT; spacing = 5.0
    }

fun EventTarget.confirmationButton(value: String, confirmationMessage: String, visible: Boolean = true, onOkAction: () -> Unit) =
    button(value) {
        isVisible = visible
        addClass(Controls.alertButton)
        action {
            alert(
                Alert.AlertType.WARNING,
                confirmationMessage,
                null,
                ButtonType.CANCEL,
                ButtonType.OK,
                actionFn = { buttonType ->
                    when (buttonType) {
                        ButtonType.OK -> onOkAction()
                        else -> Unit
                    }
                }
            )
        }
    }
