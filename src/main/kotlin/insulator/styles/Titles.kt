package insulator.styles

import javafx.scene.text.FontWeight
import tornadofx.Stylesheet
import tornadofx.cssclass
import tornadofx.px

class Titles : Stylesheet() {
    companion object {
        // Titles
        val h1 by cssclass()
        val h2 by cssclass()
        val h3 by cssclass()
        val subtitle by cssclass()
    }

    init {
        h1 {
            fontSize = 30.px
            fontWeight = FontWeight.EXTRA_BOLD
            textFill = Theme.mainColor
        }

        h2 {
            fontSize = 20.px
            fontWeight = FontWeight.EXTRA_BOLD
        }

        h3 {
            fontSize = 12.px
            textFill = Theme.darkGray
        }
    }
}
