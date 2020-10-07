package insulator.views.style

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
            prefWidth = 250.0.px
            backgroundColor = multi(theme.backgroundColor)
            backgroundColor = multi(theme.backgroundColor)
            borderInsets = multi(box(-theme.viewPadding, 0.px, -theme.viewPadding, -theme.viewPadding))
            borderColor = multi(box(Color.TRANSPARENT, theme.lightGray, Color.TRANSPARENT, Color.TRANSPARENT))
            padding = box(-theme.viewPadding, 0.px, 0.px, 0.px)
            alignment = Pos.TOP_CENTER
        }

        sidebarItem {
            and(hover) { backgroundColor = multi(theme.mainColor) }
            minHeight = 50.0.px
            borderInsets = multi(box(-theme.viewPadding, 0.px, 0.px, -theme.viewPadding))
            padding = box(theme.viewPadding)
            alignment = Pos.CENTER_LEFT
            imageView { insets(0.0, 15.0, 0.0, 0.0) }
        }
    }
}
