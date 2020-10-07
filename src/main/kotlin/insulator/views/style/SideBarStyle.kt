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
            backgroundColor = multi(Theme.backgroundColor)
            backgroundColor = multi(Theme.backgroundColor)
            borderInsets = multi(box(-Theme.viewPadding, 0.px, -Theme.viewPadding, -Theme.viewPadding))
            borderColor = multi(box(Color.TRANSPARENT, Theme.lightGray, Theme.lightGray, Theme.lightGray))
            padding = box(-Theme.viewPadding, 0.px, 0.px, 0.px)
            alignment = Pos.TOP_CENTER
        }

        sidebarItem {
            and(hover) { backgroundColor = multi(Theme.mainColor) }
            minHeight = 50.0.px
            borderInsets = multi(box(-Theme.viewPadding, 0.px, 0.px, -Theme.viewPadding))
            padding = box(Theme.viewPadding)
            alignment = Pos.CENTER_LEFT
            imageView { insets(0.0, 15.0, 0.0, 0.0) }
        }
    }
}
