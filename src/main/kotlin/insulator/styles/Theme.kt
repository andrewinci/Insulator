package insulator.styles

import javafx.scene.paint.Color
import tornadofx.Stylesheet
import tornadofx.c

class Theme : Stylesheet() {

    companion object {
        val backgroundColor: Color = Color.WHITE
        val mainColor = c("#FF9100")
        val mainColorDark = c("#D65400")
        val alertColor = c("#cc0016")
        val alertColorDark = c("#960017")
        val lightGray = c("#ccc")
        val darkGray = c("#666")
        val blueColor: Color = Color.BLUE
    }
}
