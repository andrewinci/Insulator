package insulator.viewmodel.main

import insulator.di.ClusterScope
import insulator.lib.configuration.model.Cluster
import insulator.ui.common.InsulatorTabView
import insulator.viewmodel.common.InsulatorViewModel
import insulator.views.main.schemaregistry.ListSchemaView
import insulator.views.main.topic.ListTopicView
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ObservableList
import javafx.scene.Parent
import javafx.scene.control.Alert
import javafx.scene.control.Tab
import tornadofx.* // ktlint-disable no-wildcard-imports
import javax.inject.Inject
import kotlin.reflect.KClass

@ClusterScope
class MainViewModel @Inject constructor(
    private val cluster: Cluster,
    private val listTopicView: ListTopicView,
    private val listSchemaView: ListSchemaView
) : InsulatorViewModel() {

    lateinit var contentTabs: ObservableList<Tab>
    val contentList: SimpleObjectProperty<Parent> = SimpleObjectProperty<Parent>().also { it.value = listTopicView.root }

    init {
        setParent(this)
    }

    fun <T : Any> setContentList(clazz: KClass<T>): Unit = when (clazz) {
        ListTopicView::class -> contentList.set(listTopicView.root)
        ListSchemaView::class -> {
            if (cluster.isSchemaRegistryConfigured()) contentList.set(listSchemaView.root)
            else alert(Alert.AlertType.WARNING, "Schema registry configuration not found"); Unit
        }
        else -> error.set(Throwable("UI: Unable to navigate to ${clazz.qualifiedName}"))
    }

    fun showTab(title: String, view: InsulatorTabView) {
        val existingTab = contentTabs.firstOrNull { it.content == view.root }
        val newTab = Tab(title, view.root)
            .also { it.setOnClosed { view.onTabClosed() } }
            .also { view.setOnCloseListener { it.close() } }

        existingTab?.select() ?: with(newTab) {
            contentTabs.add(this).also { this.select() }
        }
    }
}
