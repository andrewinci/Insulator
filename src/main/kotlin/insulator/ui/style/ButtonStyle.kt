package insulator.ui.style

import javafx.scene.paint.Color
import tornadofx.* // ktlint-disable no-wildcard-imports

class ButtonStyle : Stylesheet() {
    companion object {
        val button by cssclass("insulator-button")
        val alertButton by cssclass("alert-button")
        val blueButton by cssclass("blue-button")
        val burgerButton by cssclass("burger-button")
        val settingsButton by cssclass("settings-button")
    }

    init {
        button {
            padding = box(5.0.px)
            textFill = Theme.mainColor
            backgroundColor = multi(Color.TRANSPARENT)
            and(hover) {
                textFill = Theme.backgroundColor
                backgroundColor = multi(Theme.mainColor)
                and(alertButton) {
                    textFill = Theme.backgroundColor
                    backgroundColor = multi(Theme.alertColor)
                }
                and(blueButton) {
                    textFill = Theme.backgroundColor
                    backgroundColor = multi(Theme.blueColor)
                }
                backgroundRadius = multi(box(2.0.px))
            }
            and(alertButton) { textFill = Theme.alertColor }
            and(blueButton) { textFill = Theme.blueColor }
        }
        burgerButton {
            padding = box(1.em)
            textFill = Theme.mainColor
            backgroundColor = multi(Color.TRANSPARENT)
            backgroundRadius = multi(box(100.0.px))
            and(hover) {
                backgroundColor = multi(Theme.mainColorDark)
            }
        }
        settingsButton {
            padding = box(1.em)
            textFill = Theme.mainColor
            backgroundColor = multi(Color.TRANSPARENT)
            backgroundRadius = multi(box(100.0.px))
            and(hover) {
                backgroundColor = multi(Theme.mainColorDark)
            }
        }
    }
}
