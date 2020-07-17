package insulator.views.common

import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.scene.text.FontWeight
import tornadofx.*

inline fun <reified T> EventTarget.keyValueLabel(key: String, observable: ObservableValue<T>) = hbox {
    text("$key :") { style { fontWeight = FontWeight.EXTRA_BOLD } }
    label(observable)
    spacing = 2.0
}
