package insulator.styles

import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import tornadofx.Stylesheet
import tornadofx.box
import tornadofx.c
import tornadofx.multi
import tornadofx.px

class Root : Stylesheet() {
    init {
        root {
            font = Font.font("Helvetica", 10.0)
            backgroundColor = multi(Theme.backgroundColor)

            field {
                font = Font.font("Helvetica", 10.0)
            }

            textField {
                backgroundRadius = multi(box(0.0.px))
                backgroundInsets = multi(box(0.px, (-1).px, (-1).px, (-1).px), box(0.0.px), box(0.px, (-1).px, (0).px, (-1).px))
                backgroundColor = multi(Theme.mainColor, Theme.backgroundColor, c("eee"))
                and(focused) {
                    backgroundRadius = multi(box(0.0.px))
                    backgroundInsets = multi(box(0.px, (-2).px, (-2).px, (-2).px), box(0.0.px), box(0.px, (-2).px, (0).px, (-2).px))
                    backgroundColor = multi(Theme.mainColorDark, Theme.backgroundColor, c("ddd"))
                }
            }

            button {
                padding = box(5.0.px)
                textFill = Theme.mainColor
                backgroundColor = multi(Color.TRANSPARENT)
                and(hover) {
                    textFill = Theme.backgroundColor
                    backgroundColor = multi(Theme.mainColor)
                    and(Controls.alertButton) {
                        textFill = Theme.backgroundColor
                        backgroundColor = multi(Theme.alertColor)
                    }
                    backgroundRadius = multi(box(2.0.px))
                }
                and(Controls.iconButton) {
                    backgroundRadius = multi(box(100.0.px))
                    and(hover) { backgroundColor = multi(Theme.mainColorDark) }
                }
                and(Controls.alertButton) { textFill = Theme.alertColor }
            }

            checkBox {
                box {
                    focusColor = Theme.backgroundColor
                    backgroundInsets = multi(box(0.0.px))
                    backgroundColor = multi(Theme.backgroundColor)
                    borderRadius = multi(box(0.0.px))
                    borderInsets = multi(box(1.0.px))
                    borderColor = multi(box(Theme.mainColor))
                }
            }

            listView {
                focusColor = Theme.backgroundColor
                borderColor = multi(box(Theme.backgroundColor))
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
                        backgroundColor = multi(Theme.mainColor)
                    }
                }
            }

            tableView {
                borderColor = multi(box(Theme.backgroundColor))
                focusColor = Theme.backgroundColor
                and(focused) {
                    backgroundInsets = multi(box(0.0.px))
                }
                columnHeaderBackground {
                    backgroundColor = multi(Color.TRANSPARENT)
                }
                columnHeader {
                    backgroundColor = multi(Color.TRANSPARENT)
                    label {
                        textFill = Theme.mainColor
                        fontSize = 15.px
                        fontWeight = FontWeight.EXTRA_BOLD
                    }
                }
            }

            tableRowCell {
                and(selected) {
                    backgroundColor = multi(Theme.mainColor)
                }
            }

            comboBox {
                borderColor = multi(box(Theme.backgroundColor))
                focusColor = Theme.backgroundColor
                backgroundColor = multi(Color.TRANSPARENT)
                and(focused) { backgroundInsets = multi(box(0.0.px)) }
                indexedCell { textFill = Theme.mainColorDark }
                arrowButton { backgroundColor = multi(Color.TRANSPARENT) }
                arrow { backgroundColor = multi(Color.TRANSPARENT) }
                listCell { and(hover) { textFill = Theme.backgroundColor } }
            }
        }
    }
}
