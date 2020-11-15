package insulator.ui.component

import insulator.ui.style.AppBarStyle
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableObjectValue
import javafx.beans.value.ObservableStringValue
import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.layout.Priority
import tornadofx.addClass
import tornadofx.borderpane
import tornadofx.buttonbar
import tornadofx.hgrow
import tornadofx.vbox

data class AppBarBuilder(
    var title: String = "",
    var subtitle: ObservableStringValue? = null,
    var buttons: List<Button> = emptyList()
)

fun EventTarget.appBar(op: AppBarBuilder.() -> Unit) {
    val builder = AppBarBuilder().apply(op)
    this.borderpane {
        center = vbox {
            h1(builder.title)
            builder.subtitle?.let { subTitle(builder.subtitle!!) }
            alignment = Pos.CENTER_LEFT
        }
        right = buttonbar {
            buttons.addAll(builder.buttons)
        }
        addClass(AppBarStyle.appBar)
    }
}