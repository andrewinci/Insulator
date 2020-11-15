package insulator.ui.style

import javafx.geometry.Pos
import javafx.scene.paint.Color
import tornadofx.Stylesheet
import tornadofx.box
import tornadofx.cssclass
import tornadofx.em
import tornadofx.multi

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
                padding = box(0.5.em)
                textFill = theme.mainColor
                backgroundColor = multi(Color.TRANSPARENT)
                and(hover) {
                    textFill = Color.WHITE
                    backgroundColor = multi(theme.mainColor)
                    backgroundRadius = multi(box(0.5.em))
                    and(alertButton) {
                        textFill = Color.WHITE
                        backgroundColor = multi(theme.alertColor)
                    }
                    and(blueButton) {
                        textFill = Color.WHITE
                        backgroundColor = multi(theme.blueColor)
                    }
                    and(settingsButton) {
                        textFill = theme.mainColor
                        backgroundRadius = multi(box(5.em))
                        backgroundColor = multi(theme.mainColorDark)
                    }
                    and(burgerButton) {
                        textFill = theme.mainColor
                        backgroundRadius = multi(box(5.em))
                        backgroundColor = multi(theme.mainColorDark)
                    }
                }
                and(alertButton) { textFill = theme.alertColor }
                and(blueButton) { textFill = theme.blueColor }
            }
        }
    }
}
