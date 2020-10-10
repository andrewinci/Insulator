package insulator.viewmodel.main

import insulator.di.currentCluster
import insulator.viewmodel.common.InsulatorViewModel
import insulator.views.main.schemaregistry.ListSchemaView
import insulator.views.main.topic.ListTopicView
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Parent
import javafx.scene.control.Alert
import tornadofx.* // ktlint-disable no-wildcard-imports
import kotlin.reflect.KClass

class MainViewModel : InsulatorViewModel() {

    val content: SimpleObjectProperty<Parent> = SimpleObjectProperty<Parent>().also { it.value = find<ListTopicView>().root }
    val details: SimpleObjectProperty<Parent> = SimpleObjectProperty<Parent>()


    fun <T : Any> setContent(clazz: KClass<T>): Unit = when (clazz) {
        ListTopicView::class -> content.set(find<ListTopicView>().root)
        ListSchemaView::class -> {
            if (currentCluster.isSchemaRegistryConfigured()) content.set(find<ListSchemaView>().root)
            else alert(Alert.AlertType.WARNING, "Schema registry configuration not found"); Unit
        }
        else -> error.set(Throwable("UI: Unable to navigate to ${clazz.qualifiedName}"))
    }

    fun setDetails(view: View) = details.set(view.root)
}
