package insulator.viewmodel.main

import insulator.di.currentCluster
import insulator.viewmodel.common.InsulatorViewModel
import insulator.views.main.schemaregistry.ListSchemaView
import insulator.views.main.topic.ListTopicView
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ObservableList
import javafx.scene.Parent
import javafx.scene.control.Alert
import javafx.scene.control.Tab
import tornadofx.* // ktlint-disable no-wildcard-imports
import kotlin.reflect.KClass

class MainViewModel : InsulatorViewModel() {

    lateinit var contentTabs: ObservableList<Tab>
    val contentList: SimpleObjectProperty<Parent> = SimpleObjectProperty<Parent>().also { it.value = find<ListTopicView>().root }

    fun <T : Any> setContent(clazz: KClass<T>): Unit = when (clazz) {
        ListTopicView::class -> contentList.set(find<ListTopicView>().root)
        ListSchemaView::class -> {
            if (currentCluster.isSchemaRegistryConfigured()) contentList.set(find<ListSchemaView>().root)
            else alert(Alert.AlertType.WARNING, "Schema registry configuration not found"); Unit
        }
        else -> error.set(Throwable("UI: Unable to navigate to ${clazz.qualifiedName}"))
    }

    fun setContent(title: String, view: View) {
        val existingTab = contentTabs.firstOrNull { it.content == view.root }
        existingTab?.select() ?: with(Tab(title, view.root)) {
            contentTabs.add(this).also { this.select() }
        }
    }
}
