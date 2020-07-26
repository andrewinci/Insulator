package insulator.views.common

import javafx.beans.property.Property
import javafx.event.EventTarget
import javafx.geometry.Pos
import tornadofx.* // ktlint-disable no-wildcard-imports

fun EventTarget.searchBox(searchText: Property<String>) =
    hbox { label("Search"); textfield(searchText) { minWidth = 200.0 }; alignment = Pos.CENTER_RIGHT; spacing = 5.0 }
