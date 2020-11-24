package insulator.viewmodel.main

import insulator.di.ClusterComponent
import insulator.di.ClusterScope
import insulator.kafka.model.Cluster
import insulator.viewmodel.common.InsulatorViewModel
import insulator.views.main.consumergroup.ListConsumerGroupView
import insulator.views.main.schemaregistry.ListSchemaView
import insulator.views.main.topic.ListTopicView
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Parent
import javafx.scene.control.Alert
import javafx.stage.Window
import tornadofx.alert
import javax.inject.Inject
import kotlin.reflect.KClass

@ClusterScope
class MainViewModel @Inject constructor(
    private val cluster: Cluster,
    private val clusterComponent: ClusterComponent
) : InsulatorViewModel() {

    val contentList: SimpleObjectProperty<Parent> = SimpleObjectProperty<Parent>().also { it.value = clusterComponent.listTopicView().root }

    fun <T : Any> setContentList(clazz: KClass<T>, owner: Window? = null): Unit = when (clazz) {
        ListTopicView::class -> contentList.set(clusterComponent.listTopicView().root)
        ListConsumerGroupView::class -> contentList.set(clusterComponent.listConsumerGroupView().root)
        ListSchemaView::class -> {
            if (cluster.isSchemaRegistryConfigured()) contentList.set(clusterComponent.listSchemaView().root)
            else alert(Alert.AlertType.WARNING, "Schema registry configuration not found", owner = owner); Unit
        }
        else -> error.set(Throwable("UI: Unable to navigate to ${clazz.qualifiedName}"))
    }
}
