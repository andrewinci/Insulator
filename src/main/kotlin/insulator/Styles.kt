package insulator

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.text.FontWeight
import tornadofx.*


class Styles : Stylesheet() {
    companion object {
        // Define our styles
        val h1 by cssclass()
        val h2 by cssclass()

        val card by cssclass()
        val mainMenu by cssclass()

        // Define our colors
        val mainColor by cssclass()

        val dangerColor = c("#a94442")
        val hoverColor = c("#d49942")
    }


    init {
        h1 {
            fontSize = 25.px
            fontWeight = FontWeight.EXTRA_BOLD
            // padding = box(5.0.px, 0.0.px, 5.0.px, 0.0.px)
        }
        h2 {
            fontSize = 20.px
            fontWeight = FontWeight.EXTRA_BOLD
           // padding = box(5.0.px, 0.0.px, 5.0.px, 0.0.px)
        }
        mainColor {
            accentColor = Color.DARKORANGE
            textFill = Color.DARKORANGE
        }
        card {
            backgroundColor = MultiValue(arrayOf(Paint.valueOf("white")))
            backgroundRadius = multi(box(10.0.px))
            backgroundInsets = multi(box(4.0.px))
            minHeight = 100.px
            spacing = 5.px
            padding = box(10.0.px)
        }
        mainMenu {
            minWidth = 200.px
            maxWidth = 200.px
            alignment = Pos.TOP_CENTER
        }
    }
}