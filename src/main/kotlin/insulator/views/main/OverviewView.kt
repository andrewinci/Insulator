package insulator.views.main

import insulator.views.common.title
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import tornadofx.*

class OverviewView : VBox() {
    init {
        paddingAll = 5.0
        maxWidth = Double.MAX_VALUE
        title("Overview", Color.ORANGERED)
    }
}