package insulator.views.common

import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.text.FontWeight
import tornadofx.*

fun EventTarget.settingsButton(op: Button.() -> Unit = {}) = Button().attachTo(this, op) {
    it.graphic = imageview(Image(SETTINGS_ICON, 20.0, 20.0, true, true))
}

fun EventTarget.title(text: String, op: Label.() -> Unit = {}) = Label(text).attachTo(this, op) {
    it.style { fontSize = 25.px; fontWeight = FontWeight.BOLD }
    it.useMaxWidth = true
}