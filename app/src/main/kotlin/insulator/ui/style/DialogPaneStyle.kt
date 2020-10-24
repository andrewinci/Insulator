package insulator.ui.style

import javafx.scene.paint.Color
import tornadofx.* // ktlint-disable no-wildcard-imports

class DialogPaneStyle : Stylesheet() {

    init {
        dialogPane {
            backgroundColor = multi(theme.backgroundColor)
            headerPanel {
                label {
                    textFill = theme.black
                }
                backgroundColor = multi(theme.backgroundColor)
            }
            buttonBar {
                padding = box(0.0.px, (-10.0).px, 0.0.px, 0.0.px)
                cancel {
                    textFill = theme.alertColor
                    and(hover) {
                        textFill = Color.WHITE
                        backgroundColor = multi(theme.alertColor)
                    }
                }
            }
            maxHeight = 140.0.px
            prefHeight = 140.0.px
            minHeight = 140.0.px
        }
    }
}
