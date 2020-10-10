package insulator.views.style

import insulator.views.style.SideBarStyle.Companion.sidebar
import javafx.scene.paint.Color
import tornadofx.* // ktlint-disable no-wildcard-imports

class MainViewStyle : Stylesheet() {
    companion object {
        val contentList by cssclass("main-view-content-list")
        val content by cssclass("main-view-content")
        val tabContainer by cssclass("tab-container")
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
            contentList {
                padding = box(theme.viewPadding)
            }
            content {
                padding = box(0.px, 0.px, (-2).px, 0.px)
            }
        }
        tabPane {
            tabHeaderBackground {
                padding = box(0.px)
                backgroundColor = multi(theme.backgroundColor)
            }
            tabContentArea {
                padding = box(theme.viewPadding)
            }

            tabHeaderArea {
                backgroundColor = multi(theme.backgroundColor)
                padding = box(0.px)
                headersRegion {

                    tab {
                        textFill = theme.black
                        backgroundColor = multi(theme.backgroundColor)
                        backgroundRadius = multi(box(1.px))
                        backgroundInsets = multi(box(1.px))
                        borderInsets = multi(box(1.px))
                        borderRadius = multi(box(1.px))
                        borderColor = multi(box(theme.lightGray))
                        and(selected) {
                            tabContainer { tabLabel { text { fill = Color.WHITE } } }
                            backgroundColor = multi(theme.mainColor)
                        }
                        and(hover) {
                            tabContainer { tabLabel { text { fill = Color.WHITE } } }
                            backgroundColor = multi(theme.mainColorDark)
                        }
                        and(focused) { tabContainer { backgroundInsets = multi(box(0.0.px)) } }
                        focusIndicator {
                            borderColor = multi(box(Color.TRANSPARENT))
                            borderInsets = multi(box(0.px))
                        }
                    }
                }
            }
        }
    }
}
