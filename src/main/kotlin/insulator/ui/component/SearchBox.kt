package insulator.ui.component

import insulator.ui.common.InsulatorView
import insulator.ui.common.InsulatorView2
import javafx.beans.property.Property
import javafx.event.EventTarget
import javafx.geometry.Pos
import tornadofx.* // ktlint-disable no-wildcard-imports

fun EventTarget.searchBox(searchText: Property<String>, currentView: InsulatorView<*>) =
    hbox {
        fieldName("Search")
        textfield(searchText) { minWidth = 200.0 }.also { it.parent.focusedProperty().addListener(ChangeListener { _, _, _ -> it.requestFocus() }) }
        alignment = Pos.CENTER_RIGHT; spacing = 5.0
    }.also {
        if (System.getProperty("os.name")?.toString()?.toLowerCase()?.contains("mac") == true)
            currentView.shortcut("META+F") { it.requestFocus() }
        else currentView.shortcut("CTRL+F") { it.requestFocus() }
    }

fun EventTarget.searchBox2(searchText: Property<String>, currentView: InsulatorView2) =
    hbox {
        fieldName("Search")
        textfield(searchText) { minWidth = 200.0 }.also { it.parent.focusedProperty().addListener(ChangeListener { _, _, _ -> it.requestFocus() }) }
        alignment = Pos.CENTER_RIGHT; spacing = 5.0
    }.also {
        if (System.getProperty("os.name")?.toString()?.toLowerCase()?.contains("mac") == true)
            currentView.shortcut("META+F") { it.requestFocus() }
        else currentView.shortcut("CTRL+F") { it.requestFocus() }
    }
