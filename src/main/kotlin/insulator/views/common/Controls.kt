package insulator.views.common

import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.scene.text.FontWeight
import tornadofx.*

abstract class SizedView(title: String?, val viewWidth: Double?, val viewHeight: Double?) : View(title) {
    override fun onDock() {
        super.onDock()
        if (viewWidth != null) super.currentStage?.width = viewWidth
        if (viewHeight != null) super.currentStage?.height = viewHeight
    }
}

inline fun <reified T> EventTarget.keyValueLabel(key: String, observable: ObservableValue<T>) = hbox {
    text("$key :") { style { fontWeight = FontWeight.EXTRA_BOLD } }
    label(observable)
    spacing = 2.0
}
