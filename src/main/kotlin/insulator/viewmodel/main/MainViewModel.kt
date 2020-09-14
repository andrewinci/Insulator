package insulator.viewmodel.main

import insulator.di.currentCluster
import insulator.viewmodel.common.InsulatorViewModel
import insulator.views.main.schemaregistry.ListSchemaView
import insulator.views.main.topic.ListTopicView
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableObjectValue
import javafx.beans.value.ObservableStringValue
import javafx.scene.Parent
import javafx.scene.control.Alert
import tornadofx.* // ktlint-disable no-wildcard-imports
import java.util.concurrent.Callable

class MainViewModel : InsulatorViewModel() {

    val showSidebar = SimpleBooleanProperty(false)
    val currentViewProperty = SimpleObjectProperty<View>().also { it.value = find<ListTopicView>() }
    val currentCenter: ObservableObjectValue<Parent> = Bindings.createObjectBinding(Callable { currentViewProperty.value.root }, currentViewProperty)
    val currentTitle: ObservableStringValue = Bindings.createStringBinding(Callable { currentViewProperty.value.title }, currentViewProperty)

    fun toggleSidebar() = showSidebar.set(!showSidebar.value)

    fun <T> setCurrentView(clazz: Class<T>): Unit = when (clazz) {
        ListTopicView::class.java -> currentViewProperty.set(find<ListTopicView>())
        ListSchemaView::class.java -> {
            if (currentCluster.isSchemaRegistryConfigured()) currentViewProperty.set(find<ListSchemaView>())
            else alert(Alert.AlertType.WARNING, "Schema registry configuration not found"); Unit
        }
        else -> error.set(Throwable("UI: Unable to navigate to ${clazz.name}"))
    }
}
