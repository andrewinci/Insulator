package insulator.ui.style

import javafx.geometry.Pos
import javafx.scene.paint.Color
import tornadofx.* // ktlint-disable no-wildcard-imports

class AppBarStyle : Stylesheet() {
    companion object {
        val appBar by cssclass("app-bar")
        val burgerButton by cssclass("burger-button")
    }

    init {
        appBar {
            translateY = -Theme.viewPadding
            borderInsets = multi(box(-Theme.viewPadding, -Theme.viewPadding, 0.px, -Theme.viewPadding))
            spacing = 5.5.px
            alignment = Pos.CENTER_LEFT
            borderColor = multi(box(Theme.lightGray))
            minHeight = 5.em
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
    }
}
