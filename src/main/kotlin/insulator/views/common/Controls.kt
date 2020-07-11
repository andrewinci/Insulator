package insulator.views.common

import javafx.event.EventTarget
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*

fun EventTarget.settingsButton(op: Button.() -> Unit = {}) = Button().attachTo(this, op) {
    it.graphic = imageview(Image(ICON_SETTINGS, 20.0, 20.0, true, true))
}

fun EventTarget.title(text: String, color: Color = Color.BLACK, op: Label.() -> Unit = {}) = Label(text).attachTo(this, op) {
    it.style {
        fontSize = 25.px
        fontWeight = FontWeight.BOLD
        textFill = color
    }
    it.useMaxWidth = true
}

fun EventTarget.subtitle(text: String, color: Color = Color.BLACK, op: Label.() -> Unit = {}) = Label(text).attachTo(this, op) {
    it.style {
        fontSize = 20.px
        fontWeight = FontWeight.BOLD
        textFill = color
    }
    it.useMaxWidth = true
}