package insulator.views.main

import insulator.views.common.title
import javafx.geometry.Insets
import javafx.scene.layout.VBox
import javafx.scene.paint.Color

class OverviewView : VBox() {
    init {
        padding = Insets(5.0)
        maxWidth = Double.MAX_VALUE
        title("Overview", Color.ORANGERED)
    }
}