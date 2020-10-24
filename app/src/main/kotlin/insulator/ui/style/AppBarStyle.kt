package insulator.ui.style

import javafx.geometry.Pos
import javafx.scene.paint.Color
import tornadofx.Stylesheet
import tornadofx.box
import tornadofx.cssclass
import tornadofx.em
import tornadofx.multi
import tornadofx.px

class AppBarStyle : Stylesheet() {
    companion object {
        val appBar by cssclass("app-bar")
        val burgerButton by cssclass("burger-button")
    }

    init {
        appBar {
            translateY = -theme.viewPadding
            borderInsets = multi(box(-theme.viewPadding, -theme.viewPadding, 0.px, -theme.viewPadding))
            spacing = 5.0.px
            alignment = Pos.CENTER_LEFT
            borderColor = multi(box(theme.backgroundColor, theme.backgroundColor, theme.lightGray, theme.backgroundColor))
            minHeight = 5.em
        }

        burgerButton {
            padding = box(1.em)
            textFill = theme.mainColor
            backgroundColor = multi(Color.TRANSPARENT)
            backgroundRadius = multi(box(100.0.px))
            and(hover) {
                backgroundColor = multi(theme.mainColorDark)
            }
        }
    }
}
