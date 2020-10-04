package insulator.ui.component

import insulator.ui.style.TextStyle
import javafx.event.EventTarget
import tornadofx.addClass
import tornadofx.label

fun EventTarget.h1(title: String) = label(title) { addClass(TextStyle.h1) }
fun EventTarget.h2(title: String) = label(title) { addClass(TextStyle.h2) }

fun EventTarget.subTitle(subTitle: String) = label(subTitle) { addClass(TextStyle.subTitle) }
