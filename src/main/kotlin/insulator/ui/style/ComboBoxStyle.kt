package insulator.ui.style

import javafx.scene.paint.Color
import tornadofx.* // ktlint-disable no-wildcard-imports

class ComboBoxStyle : Stylesheet() {

    init {
        root {

            comboBox {
                borderColor = multi(box(Theme.backgroundColor))
                focusColor = Theme.backgroundColor
                backgroundColor = multi(Color.TRANSPARENT)
                and(focused) { backgroundInsets = multi(box(0.0.px)) }
                indexedCell { textFill = Theme.mainColorDark }
                arrowButton { backgroundColor = multi(Color.TRANSPARENT) }
                arrow { backgroundColor = multi(Color.TRANSPARENT) }
                listCell {
                    and(hover) {
                        textFill = Theme.backgroundColor
                        backgroundColor = multi(Theme.mainColor)
                    }
                }
            }
        }
    }
}
