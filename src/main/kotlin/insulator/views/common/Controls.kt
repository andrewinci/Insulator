package insulator.views.common

import javafx.beans.property.Property
import javafx.event.EventTarget
import javafx.geometry.Pos
import tornadofx.* // ktlint-disable no-wildcard-imports

fun EventTarget.searchBox(searchText: Property<String>, currentView: InsulatorView<*>) =
    hbox {
        label("Search")
        textfield(searchText) { minWidth = 200.0 }.also { it.parent.focusedProperty().addListener(ChangeListener { _, _, _ -> it.requestFocus() }) }
        alignment = Pos.CENTER_RIGHT; spacing = 5.0
    }.also {
        if (System.getProperty("os.name")?.toString()?.toLowerCase()?.contains("mac") == true)
            currentView.shortcut("META+F") { it.requestFocus() }
        else currentView.shortcut("CTRL+F") { it.requestFocus() }
    }
