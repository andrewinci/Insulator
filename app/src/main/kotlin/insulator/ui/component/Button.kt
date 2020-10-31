package insulator.ui.component

import insulator.ui.style.ButtonStyle
import insulator.ui.style.theme
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.value.ObservableValue
import javafx.event.EventHandler
import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import tornadofx.SVGIcon
import tornadofx.action
import tornadofx.addClass
import tornadofx.button

// from https://material.io/resources/icons
const val ICON_MENU_SVG = "M3 18h18v-2H3v2zm0-5h18v-2H3v2zm0-7v2h18V6H3z"
const val ICON_SETTINGS_SVG = "M19.14,12.94c0.04-0.3,0.06-0.61,0.06-0.94c0-0.32-0.02-0.64-0.07-0.94l2.03-1.58c0.18-0.14,0.23-0.41,0.12-0.61 l-1.92-3.32c-0.12-0.22-0.37-0.29-0.59-0.22l-2.39,0.96c-0.5-0.38-1.03-0.7-1.62-0.94L14.4,2.81c-0.04-0.24-0.24-0.41-0.48-0.41 h-3.84c-0.24,0-0.43,0.17-0.47,0.41L9.25,5.35C8.66,5.59,8.12,5.92,7.63,6.29L5.24,5.33c-0.22-0.08-0.47,0-0.59,0.22L2.74,8.87 C2.62,9.08,2.66,9.34,2.86,9.48l2.03,1.58C4.84,11.36,4.8,11.69,4.8,12s0.02,0.64,0.07,0.94l-2.03,1.58 c-0.18,0.14-0.23,0.41-0.12,0.61l1.92,3.32c0.12,0.22,0.37,0.29,0.59,0.22l2.39-0.96c0.5,0.38,1.03,0.7,1.62,0.94l0.36,2.54 c0.05,0.24,0.24,0.41,0.48,0.41h3.84c0.24,0,0.44-0.17,0.47-0.41l0.36-2.54c0.59-0.24,1.13-0.56,1.62-0.94l2.39,0.96 c0.22,0.08,0.47,0,0.59-0.22l1.92-3.32c0.12-0.22,0.07-0.47-0.12-0.61L19.14,12.94z M12,15.6c-1.98,0-3.6-1.62-3.6-3.6 s1.62-3.6,3.6-3.6s3.6,1.62,3.6,3.6S13.98,15.6,12,15.6z"
const val ICON_THEME_SVG = "M20 15.31L23.31 12 20 8.69V4h-4.69L12 .69 8.69 4H4v4.69L.69 12 4 15.31V20h4.69L12 23.31 15.31 20H20v-4.69zM12 18V6c3.31 0 6 2.69 6 6s-2.69 6-6 6z"

fun EventTarget.themeButton(op: () -> Unit) =
    button {
        action(op)
        graphic = SVGIcon(ICON_THEME_SVG, 20.0, theme.mainColor)
        addClass(ButtonStyle.burgerButton)
    }

fun EventTarget.settingsButton(op: () -> Unit) = button {
    graphic = SVGIcon(ICON_SETTINGS_SVG, 18)
    onMouseClicked = EventHandler { op() }
    alignment = Pos.CENTER_RIGHT
    addClass(ButtonStyle.settingsButton)
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
