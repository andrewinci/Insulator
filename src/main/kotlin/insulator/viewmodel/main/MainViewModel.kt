package insulator.viewmodel.main

import insulator.di.ClusterScope
import insulator.lib.configuration.model.Cluster
import insulator.viewmodel.common.InsulatorViewModel
import insulator.views.main.schemaregistry.ListSchemaView
import insulator.views.main.topic.ListTopicView
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Parent
import javafx.scene.control.Alert
import tornadofx.* // ktlint-disable no-wildcard-imports
import javax.inject.Inject
import kotlin.reflect.KClass

@ClusterScope
class MainViewModel @Inject constructor(
    private val cluster: Cluster,
    private val listTopicView: ListTopicView, //todo: replace with a factory
    private val listSchemaView: ListSchemaView
) : InsulatorViewModel() {

    val contentList: SimpleObjectProperty<Parent> = SimpleObjectProperty<Parent>().also { it.value = listTopicView.root }

    fun <T : Any> setContentList(clazz: KClass<T>): Unit = when (clazz) {
        ListTopicView::class -> contentList.set(listTopicView.root)
        ListSchemaView::class -> {
            if (cluster.isSchemaRegistryConfigured()) contentList.set(listSchemaView.root)
            else alert(Alert.AlertType.WARNING, "Schema registry configuration not found"); Unit
        }
        else -> error.set(Throwable("UI: Unable to navigate to ${clazz.qualifiedName}"))
    }
}
