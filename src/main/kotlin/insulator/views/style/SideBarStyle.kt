package insulator.views.style

import insulator.views.style.TextStyle.Companion.h1
import javafx.geometry.Pos
import javafx.scene.paint.Color
import tornadofx.* // ktlint-disable no-wildcard-imports

class SideBarStyle : Stylesheet() {
    companion object {
        val sidebar by cssclass("sidebar")
        val sidebarItem by cssclass("sidebar-item")
    }

    init {
        sidebar {
            spacing = 1.em
            minWidth = 250.0.px
            maxWidth = 250.0.px
            backgroundColor = multi(theme.backgroundColor)
            alignment = Pos.TOP_CENTER
            padding = box(0.em, 0.em, 1.em, 0.em)
            h1 {
                minHeight = 3.em
            }
        }

        sidebarItem {
            and(hover) { backgroundColor = multi(theme.mainColor) }
            minHeight = 50.0.px
            borderInsets = multi(box(-theme.viewPadding, 0.px, 0.px, -theme.viewPadding))
            alignment = Pos.CENTER_LEFT
            padding = box(1.em)
        }
    }
}
