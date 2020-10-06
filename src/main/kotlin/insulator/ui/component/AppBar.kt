package insulator.ui.component

import insulator.ui.style.AppBarStyle
import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import tornadofx.* // ktlint-disable no-wildcard-imports

fun EventTarget.appBar(op: EventTarget.() -> Unit) =
    vbox(alignment = Pos.TOP_LEFT) {
        op()
        // hbox { addClass(AppBarStyle.topBarMenuShadow) }
        hgrow = Priority.ALWAYS
        addClass(AppBarStyle.appBar)
    }
