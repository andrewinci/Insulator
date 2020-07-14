package insulator.views.common

import javafx.event.EventTarget
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
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

fun EventTarget.card(title: String? = null, width: Double? = null, op: VBox.() -> Unit = {}) = vbox {
    if (title != null) title(title, Color.ORANGERED) { paddingAll = 5.0 }
    paddingAll = 5.0;
    if (width != null) {
        maxWidth = width; minWidth = width
    }
    background = Background(BackgroundFill(Paint.valueOf("white"), CornerRadii(10.0), Insets(4.0)))
    minHeight = 100.0
    spacing = 5.0
    opcr(this, VBox(), op)
}

abstract class SizedView(title: String?, val viewWidth: Double?, val viewHeight: Double?) : View(title) {
    override fun onDock() {
        super.onDock()
        if (viewWidth != null) super.currentStage?.width = viewWidth
        if (viewHeight != null) super.currentStage?.height = viewHeight
    }
}