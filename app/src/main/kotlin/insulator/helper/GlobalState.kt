package insulator.helper

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.value.ObservableValue
import javafx.scene.Node
import tornadofx.visibleWhen

object GlobalState {
    val isReadOnlyProperty = SimpleBooleanProperty(true)
}

fun <T : Node> T.hideOnReadonly() : T {
    this.visibleWhen { GlobalState.isReadOnlyProperty.not() }
    this.managedProperty().bind(GlobalState.isReadOnlyProperty.not())
    return this
}