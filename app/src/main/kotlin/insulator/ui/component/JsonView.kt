package insulator.ui.component

import insulator.helper.createListBindings
import insulator.jsonhelper.JsonFormatter
import insulator.jsonhelper.Token
import insulator.ui.style.theme
import javafx.beans.value.ObservableStringValue
import javafx.event.EventTarget
import javafx.scene.input.Clipboard
import javafx.scene.layout.Priority
import javafx.scene.text.Font
import javafx.scene.text.Text
import tornadofx.action
import tornadofx.bind
import tornadofx.contextmenu
import tornadofx.item
import tornadofx.putString
import tornadofx.scrollpane
import tornadofx.text
import tornadofx.textflow
import tornadofx.vgrow

fun EventTarget.jsonView(value: ObservableStringValue, formatter: JsonFormatter) {
    val tokens = createListBindings(
        {
            formatter.formatJsonString(value.value)
                .map { records -> records.map { mapJsonTokenToFxText(it) } }
                .fold({ listOf(text(value)) }, { it })
        },
        value
    )
    this.scrollpane {
        textflow {
            children.bind(tokens) { it }
            contextMenu = contextmenu { item("Copy") { action { Clipboard.getSystemClipboard().putString(value.value) } } }
        }
        vgrow = Priority.ALWAYS
        minHeight = 50.0
    }
}

private fun EventTarget.mapJsonTokenToFxText(it: Token): Text =
    text(it.text) {
        fill = when (it) {
            is Token.Symbol -> theme.darkGray
            is Token.Key -> theme.blueColor
            is Token.Value -> theme.greenColor
        }
        font = Font.font("Helvetica", 15.0)
    }
