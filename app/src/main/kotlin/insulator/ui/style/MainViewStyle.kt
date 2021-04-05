package insulator.ui.style

import javafx.geometry.Pos
import javafx.scene.paint.Color
import tornadofx.Stylesheet
import tornadofx.box
import tornadofx.cssclass
import tornadofx.em
import tornadofx.multi
import tornadofx.px

class MainViewStyle : Stylesheet() {
    companion object {
        val contentList by cssclass("main-view-content-list")
        val content by cssclass("main-view-content")
        val tabContainer by cssclass("tab-container")
        val sidebar by cssclass("sidebar")
        val sidebarItem by cssclass("sidebar-item")
        val clusterOptions by cssclass("cluster-options")
    }

    private val contentPadding = 0.1.em

    init {
        sidebar {
            backgroundColor = multi(theme.backgroundColor)
            alignment = Pos.TOP_LEFT
            padding = box(0.em, 0.em, 1.em, 0.em)
        }

        clusterOptions {
            padding = box(-0.5.em,theme.viewPadding,theme.viewPadding,theme.viewPadding)
            minHeight = 5.em
        }

        sidebarItem {
            and(hover) { backgroundColor = multi(theme.mainColor) }
            minHeight = 50.0.px
            borderInsets = multi(box(-theme.viewPadding, 0.px, 0.px, -theme.viewPadding))
            alignment = Pos.CENTER_LEFT
            padding = box(1.em)
        }

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
                padding = box(theme.viewPadding + contentPadding)
            }
            tabHeaderArea {
                backgroundColor = multi(theme.backgroundColor)
                padding = box(0.px)
                headersRegion {

                    tab {
                        tabContainer { tabLabel { text { fill = theme.black } } }
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
