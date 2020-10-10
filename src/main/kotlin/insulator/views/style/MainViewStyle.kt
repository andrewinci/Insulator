package insulator.views.style

import insulator.views.style.SideBarStyle.Companion.sidebar
import tornadofx.* // ktlint-disable no-wildcard-imports

class MainViewStyle : Stylesheet() {
    companion object {
        val subview by cssclass("main-sub-view")
    }

    init {
        splitPane {
            padding = box(0.px)
            splitPaneDivider {
                padding = box(0.px, 1.px, 0.px, 0.px)
            }
            sidebar {
                padding = box(theme.viewPadding, 0.px)
            }
            subview {
                padding = box(theme.viewPadding)
            }
        }
    }
}
