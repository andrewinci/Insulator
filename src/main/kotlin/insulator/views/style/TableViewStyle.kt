package insulator.views.style

import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.* // ktlint-disable no-wildcard-imports

class TableViewStyle : Stylesheet() {

    init {
        root {
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
        }
    }
}
