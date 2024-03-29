package insulator.ui.component

import insulator.helper.dispatch
import insulator.ui.style.ButtonStyle
import insulator.ui.style.theme
import javafx.beans.binding.Bindings
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.ButtonType
import javafx.scene.paint.Color
import tornadofx.SVGIcon
import tornadofx.action
import tornadofx.addClass
import tornadofx.button
import tornadofx.enableWhen
import tornadofx.onChange
import tornadofx.toggleClass

// from https://material.io/resources/icons
const val ICON_MENU_SVG = "M3 18h18v-2H3v2zm0-5h18v-2H3v2zm0-7v2h18V6H3z"
const val ICON_SETTINGS_SVG = "M19.14,12.94c0.04-0.3,0.06-0.61,0.06-0.94c0-0.32-0.02-0.64-0.07-0.94l2.03-1.58c0.18-0.14,0.23-0.41,0.12-0.61 l-1.92-3.32c-0.12-0.22-0.37-0.29-0.59-0.22l-2.39,0.96c-0.5-0.38-1.03-0.7-1.62-0.94L14.4,2.81c-0.04-0.24-0.24-0.41-0.48-0.41 h-3.84c-0.24,0-0.43,0.17-0.47,0.41L9.25,5.35C8.66,5.59,8.12,5.92,7.63,6.29L5.24,5.33c-0.22-0.08-0.47,0-0.59,0.22L2.74,8.87 C2.62,9.08,2.66,9.34,2.86,9.48l2.03,1.58C4.84,11.36,4.8,11.69,4.8,12s0.02,0.64,0.07,0.94l-2.03,1.58 c-0.18,0.14-0.23,0.41-0.12,0.61l1.92,3.32c0.12,0.22,0.37,0.29,0.59,0.22l2.39-0.96c0.5,0.38,1.03,0.7,1.62,0.94l0.36,2.54 c0.05,0.24,0.24,0.41,0.48,0.41h3.84c0.24,0,0.44-0.17,0.47-0.41l0.36-2.54c0.59-0.24,1.13-0.56,1.62-0.94l2.39,0.96 c0.22,0.08,0.47,0,0.59-0.22l1.92-3.32c0.12-0.22,0.07-0.47-0.12-0.61L19.14,12.94z M12,15.6c-1.98,0-3.6-1.62-3.6-3.6 s1.62-3.6,3.6-3.6s3.6,1.62,3.6,3.6S13.98,15.6,12,15.6z"
const val ICON_THEME_SVG = "M20 15.31L23.31 12 20 8.69V4h-4.69L12 .69 8.69 4H4v4.69L.69 12 4 15.31V20h4.69L12 23.31 15.31 20H20v-4.69zM12 18V6c3.31 0 6 2.69 6 6s-2.69 6-6 6z"
const val ICON_LOCK_SVG = "M18 8h-1V6c0-2.76-2.24-5-5-5S7 3.24 7 6v2H6c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V10c0-1.1-.9-2-2-2zm-6 9c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2zm3.1-9H8.9V6c0-1.71 1.39-3.1 3.1-3.1 1.71 0 3.1 1.39 3.1 3.1v2z"
const val ICON_UNLOCK_SVG = "M12 17c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm6-9h-1V6c0-2.76-2.24-5-5-5S7 3.24 7 6h1.9c0-1.71 1.39-3.1 3.1-3.1 1.71 0 3.1 1.39 3.1 3.1v2H6c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V10c0-1.1-.9-2-2-2zm0 12H6V10h12v10z"

fun EventTarget.refreshButton(name: String, isEnabled: BooleanProperty, refreshOp: suspend () -> Unit) =
    button("Refresh") {
        id = "button-refresh-$name"
        enableWhen(isEnabled)
        action { dispatch { refreshOp() } }
        addClass(ButtonStyle.blueButton)
    }

fun EventTarget.refreshButton(name: String, refreshOp: suspend () -> Unit) =
    refreshButton(name, SimpleBooleanProperty(true), refreshOp)

fun EventTarget.readOnlyButton(isReadOnlyProperty: BooleanProperty): Button {
    val getIcon = { if (isReadOnlyProperty.value) ICON_LOCK_SVG else ICON_UNLOCK_SVG }
    val color = { if (!isReadOnlyProperty.value) Color.RED else theme.mainColor }
    val text = { if (isReadOnlyProperty.value) "ReadOnly mode" else "Read/Write mode" }
    val button = button {
        textProperty().bind(Bindings.createStringBinding(text, isReadOnlyProperty))
        action { isReadOnlyProperty.set(isReadOnlyProperty.not().value) }
        graphicProperty().bind(
            Bindings.createObjectBinding({ SVGIcon(getIcon(), 22.0, color()) }, isReadOnlyProperty)
        )
        addClass(ButtonStyle.toggleButton)
    }
    isReadOnlyProperty.onChange {
        button.toggleClass(ButtonStyle.alertButton, !isReadOnlyProperty.value)
    }
    return button
}

fun EventTarget.themeButton(op: () -> Unit) =
    button {
        text = "Change theme"
        action(op)
        graphic = SVGIcon(ICON_THEME_SVG, 22.0, theme.mainColor)
        addClass(ButtonStyle.toggleButton)
    }

fun EventTarget.clusterSettingsButton(op: () -> Unit) = button {
    graphic = SVGIcon(ICON_SETTINGS_SVG, 18)
    action(op)
    alignment = Pos.CENTER_RIGHT
    addClass(ButtonStyle.settingsButton)
}

fun EventTarget.configurationButton(op: () -> Unit) = button {
    text = "Settings"
    graphic = SVGIcon(ICON_SETTINGS_SVG, 18, theme.mainColor)
    action(op)
    addClass(ButtonStyle.toggleButton)
}

fun EventTarget.confirmationButton(value: String, confirmationMessage: String, visible: Boolean = true, onOkAction: () -> Unit) =
    confirmationButton(value, confirmationMessage, SimpleBooleanProperty(visible), onOkAction)

fun EventTarget.confirmationButton(value: String, confirmationMessage: String, visibleProperty: ObservableValue<Boolean>, onOkAction: () -> Unit) =
    button(value) {
        visibleProperty().bind(visibleProperty)
        addClass(ButtonStyle.alertButton)
        action {
            insulatorAlert(Alert.AlertType.WARNING, confirmationMessage, ButtonType.CANCEL, ButtonType.OK) { btnType ->
                when (btnType) {
                    ButtonType.OK -> onOkAction()
                    else -> Unit
                }
            }
        }
    }
