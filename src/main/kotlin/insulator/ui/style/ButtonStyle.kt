package insulator.ui.style

import javafx.scene.paint.Color
import tornadofx.* // ktlint-disable no-wildcard-imports

class ButtonStyle : Stylesheet() {
    companion object {
        val alertButton by cssclass("alert-button")
        val blueButton by cssclass("blue-button")
        val burgerButton by cssclass("burger-button")
        val settingsButton by cssclass("settings-button")
    }

    init {
        root {
            button {
                padding = box(0.7.em)
                textFill = Theme.mainColor
                backgroundColor = multi(Color.TRANSPARENT)
                and(hover) {
                    textFill = Theme.backgroundColor
                    backgroundColor = multi(Theme.mainColor)
                    backgroundRadius = multi(box(0.7.em))
                    and(alertButton) {
                        textFill = Theme.backgroundColor
                        backgroundColor = multi(Theme.alertColor)
                    }
                    and(blueButton) {
                        textFill = Theme.backgroundColor
                        backgroundColor = multi(Theme.blueColor)
                    }
                    and(settingsButton) {
                        textFill = Theme.mainColor
                        backgroundRadius = multi(box(5.em))
                        backgroundColor = multi(Theme.mainColorDark)
                    }
                    and(burgerButton) {
                        textFill = Theme.mainColor
                        backgroundRadius = multi(box(5.em))
                        backgroundColor = multi(Theme.mainColorDark)
                    }
                }
                and(alertButton) { textFill = Theme.alertColor }
                and(blueButton) { textFill = Theme.blueColor }
            }
        }
    }
}
