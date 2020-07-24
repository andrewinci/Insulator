package insulator.views.common

import javafx.beans.property.Property
import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.text.FontWeight
import tornadofx.*

inline fun <reified T> EventTarget.keyValueLabel(key: String, observable: ObservableValue<T>) = hbox {
    text("$key :") { style { fontWeight = FontWeight.EXTRA_BOLD } }
    label(observable)
    spacing = 2.0
}

fun EventTarget.searchBox(searchText: Property<String>) =
    hbox { label("Search"); textfield(searchText) { minWidth = 200.0 }; alignment = Pos.CENTER_RIGHT; spacing = 5.0 }
