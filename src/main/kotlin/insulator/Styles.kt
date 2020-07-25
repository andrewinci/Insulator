package insulator

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.effect.Blend
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import tornadofx.*


class Styles : Stylesheet() {
    //this effect is not supported by tornadofx and it will set the effect to null
    private val nullEffect = Blend()
    private val defaultPadding = 15.0.px

    companion object {
        // Titles
        val h1 by cssclass()
        val h2 by cssclass()
        val h3 by cssclass()

        // Components
        val topBarMenu by cssclass()
        val subtitle by cssclass()
        val topBarMenuShadow by cssclass()
        val sidebar by cssclass()
        val sidebarItem by cssclass()
        val iconButton by cssclass()
        val alertButton by cssclass()

        // Colors
        val mainColor = c("#FF9100")
        val mainColorDark = c("D6670D")
        val alertColor = c("#cc0016")
        val alertColorDark = c("#960017")
        val lightGray = c("#ccc")
        val darkGray = c("#666")
    }

    init {

        h1 {
            fontSize = 30.px
            fontWeight = FontWeight.EXTRA_BOLD
            textFill = mainColor
        }

        h2 {
            fontSize = 20.px
            fontWeight = FontWeight.EXTRA_BOLD
        }

        h3 {
            fontSize = 12.px
            textFill = darkGray
        }

        topBarMenu {
            translateY = -defaultPadding
            borderInsets = multi(box(-defaultPadding, -defaultPadding, 0.px, -defaultPadding))
            spacing = 5.px
            alignment = Pos.CENTER_LEFT
            borderColor = multi(box(lightGray))
            prefHeight = 60.0.px
            and(subtitle) { prefHeight = 70.0.px }
        }

        topBarMenuShadow {
            translateY = -defaultPadding
            prefHeight = 0.0.px
            borderColor = multi(box(lightGray))
            effect = DropShadow(1.0, 0.0, +1.0, lightGray)
            borderInsets = multi(box(0.0.px, -defaultPadding))
        }

        sidebar {
            prefWidth = 250.0.px
            backgroundColor = multi(Color.WHITE)
            borderInsets = multi(box(-defaultPadding, 0.px, -defaultPadding, -defaultPadding))
            borderColor = multi(box(lightGray))
            padding = box(-defaultPadding, 0.px, 0.px, 0.px)
            alignment = Pos.TOP_CENTER
        }

        sidebarItem {
            and(hover) { backgroundColor = multi(mainColor) }
            minHeight = 50.0.px
            borderInsets = multi(box(-defaultPadding, 0.px, 0.px, -defaultPadding))
            padding = box(defaultPadding)
            alignment = Pos.CENTER_LEFT
            imageView { insets(0.0, 15.0, 0.0, 0.0) }
        }

        dialogPane {
            headerPanel {
                borderColor = multi(box(Color.WHITE))
                backgroundColor = multi(Color.WHITE)
                backgroundInsets = multi(box(0.0.px))
                padding = box(0.0.px)
            }
            buttonBar {
                padding = box(0.0.px, (-10.0).px, 0.0.px, 0.0.px)
                cancel {
                    textFill = alertColor
                    and(hover) {
                        textFill = Color.WHITE
                        backgroundColor = multi(alertColor)
                    }
                }
            }
            maxHeight = 140.0.px
            prefHeight = 140.0.px
            minHeight = 140.0.px
        }

        root {
            padding = box(defaultPadding)
            font = Font.font("Helvetica", 10.0)
            backgroundColor = multi(Color.WHITE)

            field {
                font = Font.font("Helvetica", 10.0)
            }

            textField {
                backgroundRadius = multi(box(0.0.px))
                backgroundInsets = multi(box(0.px, (-1).px, (-1).px, (-1).px), box(0.0.px), box(0.px, (-1).px, (0).px, (-1).px))
                backgroundColor = multi(mainColor, Color.WHITE, c("eee"))
                and(focused) {
                    backgroundRadius = multi(box(0.0.px))
                    backgroundInsets = multi(box(0.px, (-2).px, (-2).px, (-2).px), box(0.0.px), box(0.px, (-2).px, (0).px, (-2).px))
                    backgroundColor = multi(mainColorDark, Color.WHITE, c("ddd"))
                }
            }

            button {
                padding = box(10.0.px)
                textFill = mainColor
                backgroundColor = multi(Color.TRANSPARENT)
                and(hover) {
                    textFill = Color.WHITE
                    backgroundColor = multi(mainColor)
                    and(alertButton) {
                        textFill = Color.WHITE
                        backgroundColor = multi(alertColor)
                    }
                    backgroundRadius = multi(box(2.0.px))
                }
                and(iconButton) {
                    backgroundRadius = multi(box(100.0.px))
                }
                and(alertButton) { textFill = alertColor }
            }

            checkBox {
                box {
                    focusColor = Color.WHITE
                    backgroundInsets = multi(box(0.0.px))
                    backgroundColor = multi(Color.WHITE)
                    borderRadius = multi(box(0.0.px))
                    borderInsets = multi(box(1.0.px))
                    borderColor = multi(box(mainColor))
                }
            }

            listView {
                focusColor = Color.WHITE
                borderColor = multi(box(Color.WHITE))
                backgroundInsets = multi(box(0.0.px))
                and(focused) {
                    borderRadius = multi(box(30.0.px))
                    backgroundInsets = multi(box(0.0.px))
                }
                listCell {
                    padding = box(10.0.px)
                    borderRadius = multi(box(30.0.px))
                    label { textFill = Color.BLACK }
                    and(even) { backgroundColor = multi(Color.TRANSPARENT) }
                    and(odd) { backgroundColor = multi(Color.TRANSPARENT) }
                    and(hover) {
                        backgroundColor = multi(mainColor)
                    }
                }
            }

            tableView {
                borderColor = multi(box(Color.WHITE))
                focusColor = Color.WHITE
                and(focused) {
                    backgroundInsets = multi(box(0.0.px))
                }
                columnHeaderBackground {
                    backgroundColor = multi(Color.TRANSPARENT)
                }
                columnHeader {
                    backgroundColor = multi(Color.TRANSPARENT)
                    label {
                        textFill = mainColor
                        fontSize = 15.px
                        fontWeight = FontWeight.EXTRA_BOLD
                    }

                }
            }

            tableRowCell {
                and(selected) {
                    backgroundColor = multi(mainColor)
                }
            }

            comboBox {
                borderColor = multi(box(Color.WHITE))
                focusColor = Color.WHITE
                backgroundColor = multi(Color.TRANSPARENT)
                and(focused) { backgroundInsets = multi(box(0.0.px)) }
                indexedCell { textFill = mainColorDark }
                arrowButton { backgroundColor = multi(Color.TRANSPARENT) }
                arrow { backgroundColor = multi(Color.TRANSPARENT) }
                listCell { and(hover) { textFill = Color.WHITE } }
            }
        }

    }
}