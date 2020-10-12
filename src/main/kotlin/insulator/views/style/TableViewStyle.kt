package insulator.views.style

import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.* // ktlint-disable no-wildcard-imports

class TableViewStyle : Stylesheet() {
    init {
        tableView {
            borderColor = multi(box(theme.backgroundColor))
            backgroundColor = multi(theme.backgroundColor)
            focusColor = theme.backgroundColor
            label {
                textFill = theme.lightGray
            }
            and(focused) {
                backgroundInsets = multi(box(0.0.px))
            }
            columnHeaderBackground {
                backgroundColor = multi(theme.backgroundColor)
                filler {
                    backgroundColor = multi(theme.backgroundColor)
                }
            }
            columnHeader {
                backgroundColor = multi(theme.backgroundColor)
                label {
                    textFill = theme.mainColor
                    fontSize = 15.px
                    fontWeight = FontWeight.EXTRA_BOLD
                }
            }
        }

        tableRowCell {
            cell { text { fill = theme.black } }
            and(selected) {
                cell { backgroundColor = multi(theme.mainColor) }
                cell { text { fill = Color.WHITE } }
            }
            and(even) { backgroundColor = multi(theme.backgroundColor) }
            and(odd) { backgroundColor = multi(theme.backgroundColor) }
        }
    }
}
