package insulator.views.style

import javafx.scene.paint.Color
import tornadofx.* // ktlint-disable no-wildcard-imports

class ComboBoxStyle : Stylesheet() {

    init {
        root {

            comboBox {
                borderColor = multi(box(theme.backgroundColor))
                focusColor = theme.backgroundColor
                backgroundColor = multi(Color.TRANSPARENT)
                and(focused) { backgroundInsets = multi(box(0.0.px)) }
                indexedCell { textFill = theme.mainColorDark }
                arrowButton { backgroundColor = multi(Color.TRANSPARENT) }
                arrow { backgroundColor = multi(Color.TRANSPARENT) }
                listCell {
                    and(hover) {
                        textFill = Color.WHITE
                        backgroundColor = multi(theme.mainColor)
                    }
                }
                label { textFill = theme.black }
            }
        }
    }
}
