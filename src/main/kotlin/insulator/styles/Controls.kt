package insulator.styles

import javafx.geometry.Pos
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import tornadofx.Stylesheet
import tornadofx.box
import tornadofx.cssclass
import tornadofx.insets
import tornadofx.multi
import tornadofx.px

class Controls : Stylesheet() {
    private val defaultPadding = 12.0.px

    companion object {

        // Components
        val topBarMenu by cssclass()
        val topBarMenuShadow by cssclass()
        val sidebar by cssclass()
        val sidebarItem by cssclass()
        val iconButton by cssclass()
        val alertButton by cssclass()
        val blueButton by cssclass()
        val view by cssclass()
    }

    init {

        topBarMenu {
            translateY = -defaultPadding
            borderInsets = multi(box(-defaultPadding, -defaultPadding, 0.px, -defaultPadding))
            spacing = 5.px
            alignment = Pos.CENTER_LEFT
            borderColor = multi(box(Theme.lightGray))
            prefHeight = 60.0.px
            and(Titles.subtitle) { prefHeight = 70.0.px }
        }

        topBarMenuShadow {
            translateY = -defaultPadding
            prefHeight = 0.0.px
            borderColor = multi(box(Theme.lightGray))
            effect = DropShadow(1.0, 0.0, +1.0, Theme.lightGray)
            borderInsets = multi(box(0.0.px, -defaultPadding))
        }

        sidebar {
            prefWidth = 250.0.px
            backgroundColor = multi(Theme.backgroundColor)
            backgroundColor = multi(Theme.backgroundColor)
            borderInsets = multi(box(-defaultPadding, 0.px, -defaultPadding, -defaultPadding))
            borderColor = multi(box(Theme.lightGray))
            padding = box(-defaultPadding, 0.px, 0.px, 0.px)
            alignment = Pos.TOP_CENTER
        }

        sidebarItem {
            and(hover) { backgroundColor = multi(Theme.mainColor) }
            minHeight = 50.0.px
            borderInsets = multi(box(-defaultPadding, 0.px, 0.px, -defaultPadding))
            padding = box(defaultPadding)
            alignment = Pos.CENTER_LEFT
            imageView { insets(0.0, 15.0, 0.0, 0.0) }
        }

        scrollPane {
            focusColor = Theme.backgroundColor
            focusColor = Theme.backgroundColor
            borderWidth = multi(box(0.0.px))
            borderColor = multi(box(Theme.backgroundColor))
            borderColor = multi(box(Theme.backgroundColor))
            backgroundInsets = multi(box(0.0.px))
            backgroundColor = multi(Theme.backgroundColor)
            backgroundColor = multi(Theme.backgroundColor)
            and(focused) { backgroundInsets = multi(box(0.0.px)) }
            viewport {
                borderColor = multi(box(Theme.backgroundColor))
                borderColor = multi(box(Theme.backgroundColor))
                backgroundInsets = multi(box(0.0.px))
                backgroundColor = multi(Theme.backgroundColor)
                backgroundColor = multi(Theme.backgroundColor)
            }
            corner {
                borderColor = multi(box(Theme.backgroundColor))
                borderColor = multi(box(Theme.backgroundColor))
                backgroundInsets = multi(box(0.0.px))
                backgroundColor = multi(Theme.backgroundColor)
                backgroundColor = multi(Theme.backgroundColor)
            }
        }

        dialogPane {
            padding = box(defaultPadding)
            headerPanel {
                borderColor = multi(box(Theme.backgroundColor))
                borderColor = multi(box(Theme.backgroundColor))
                backgroundColor = multi(Theme.backgroundColor)
                backgroundColor = multi(Theme.backgroundColor)
                backgroundInsets = multi(box(0.0.px))
                padding = box(0.0.px)
            }
            buttonBar {
                padding = box(0.0.px, (-10.0).px, 0.0.px, 0.0.px)
                cancel {
                    textFill = Theme.alertColor
                    and(hover) {
                        textFill = Theme.backgroundColor
                        textFill = Theme.backgroundColor
                        backgroundColor = multi(Theme.alertColor)
                    }
                }
            }
            maxHeight = 140.0.px
            prefHeight = 140.0.px
            minHeight = 140.0.px
        }

        view { padding = box(defaultPadding) }

        contextMenu {
            padding = box(0.px)
            minWidth = 100.0.px
            textFill = Color.BLACK
            menuItem {
                padding = box(10.0.px)
                and(focused) {
                    backgroundColor = multi(Theme.mainColor)
                    label {
                        textFill = Theme.backgroundColor
                        textFill = Theme.backgroundColor
                    }
                }
            }
        }
    }
}
