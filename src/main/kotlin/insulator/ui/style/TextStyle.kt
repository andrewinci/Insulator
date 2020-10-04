package insulator.ui.style

import javafx.scene.text.FontWeight
import tornadofx.* // ktlint-disable no-wildcard-imports

class TextStyle : Stylesheet() {
    companion object {
        val h1 by cssclass("h1")
        val h2 by cssclass("h2")
        val subTitle by cssclass("sub-title")
    }

    init {
        h1 {
            fontSize = 2.em
            fontWeight = FontWeight.EXTRA_BOLD
            textFill = Theme.mainColor
            padding = box(0.2.em, 0.2.em, 0.em, 0.2.em)
        }
        h2 {
            fontSize = 1.5.em
            fontWeight = FontWeight.BOLD
            textFill = Theme.black
            padding = box(0.2.em, 0.2.em, 0.em, 0.2.em)
        }
        subTitle {
            fontSize = 1.em
            fontWeight = FontWeight.NORMAL
            textFill = Theme.lightGray
            padding = box(0.1.em)
        }
    }
}
