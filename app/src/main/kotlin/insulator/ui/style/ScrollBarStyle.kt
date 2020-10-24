package insulator.ui.style

import javafx.scene.paint.Color
import tornadofx.Stylesheet
import tornadofx.box
import tornadofx.em
import tornadofx.multi
import tornadofx.px

class ScrollBarStyle : Stylesheet() {

    init {
        root {
            scrollBar {
                backgroundColor = multi(Color.TRANSPARENT)
                track {
                    backgroundColor = multi(Color.TRANSPARENT)
                    borderColor = multi(box(Color.TRANSPARENT))
                    backgroundRadius = multi(box(0.0.em))
                    borderRadius = multi(box(2.0.em))
                }

                incrementButton {
                    backgroundColor = multi(Color.TRANSPARENT)
                    backgroundRadius = multi(box(0.0.em))
                    padding = box(0.0.px, 0.0.px, 0.0.px, 0.0.px)
                }

                decrementButton {
                    backgroundColor = multi(Color.TRANSPARENT)
                    backgroundRadius = multi(box(0.0.em))
                    padding = box(0.0.px, 0.0.px, 0.0.px, 0.0.px)
                }

                incrementArrow { shape = " "; backgroundColor = multi(Color.TRANSPARENT) }
                decrementArrow { shape = " "; backgroundColor = multi(Color.TRANSPARENT) }

                and(vertical) {
                    incrementArrow { padding = box(0.em, 0.4.em) }
                    decrementArrow { padding = box(0.em, 0.4.em) }
                }
                and(horizontal) {
                    incrementArrow { padding = box(0.4.em, 0.0.em) }
                    decrementArrow { padding = box(0.4.em, 0.0.em) }
                }

                thumb {
                    backgroundColor = multi(theme.darkGray)
                    backgroundRadius = multi(box(1.0.em))
                }
            }
        }
    }
}
