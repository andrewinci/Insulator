package insulator.views.component

import insulator.views.style.TextStyle
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableStringValue
import javafx.event.EventTarget
import javafx.scene.control.Label
import tornadofx.Stylesheet
import tornadofx.addClass
import tornadofx.label

fun EventTarget.h1(title: String) = label(title) { addClass(TextStyle.h1) }
fun EventTarget.h1(title: ObservableStringValue) = label(title) { addClass(TextStyle.h1) }

fun EventTarget.h2(title: String) = label(title) { addClass(TextStyle.h2) }

fun EventTarget.subTitle(subTitle: String, op: Label.() -> Unit = {}) = subTitle(SimpleStringProperty(subTitle), op)
fun EventTarget.subTitle(subTitle: ObservableStringValue, op: Label.() -> Unit = {}) = label(subTitle) { op(); addClass(TextStyle.subTitle) }

fun EventTarget.fieldName(text: String) = label(text) { addClass(Stylesheet.field)}
