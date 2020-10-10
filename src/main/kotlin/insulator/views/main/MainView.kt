package insulator.views.main

import insulator.di.currentCluster
import insulator.viewmodel.main.MainViewModel
import insulator.views.common.ICON_REGISTRY
import insulator.views.common.ICON_TOPICS
import insulator.views.common.InsulatorView
import insulator.views.component.h1
import insulator.views.component.h2
import insulator.views.configurations.ListClusterView
import insulator.views.main.schemaregistry.ListSchemaView
import insulator.views.main.topic.ListTopicView
import insulator.views.style.MainViewStyle.Companion.subview
import insulator.views.style.SideBarStyle
import javafx.beans.value.ObservableObjectValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.event.EventTarget
import javafx.scene.Parent
import javafx.scene.image.Image
import tornadofx.* // ktlint-disable no-wildcard-imports

class MainView : InsulatorView<MainViewModel>("Insulator", MainViewModel::class) {

    private val content = viewWrapper(viewModel.content, 750.0)
    private val contentList = viewWrapper(viewModel.contentList, 500.0, 500.0)
    private val nodes: ObservableList<Parent> = FXCollections.observableArrayList(
        sidebar(),
        contentList
    )

    override val root = splitpane { items.bind(nodes) { it } }

    init {
        content.centerProperty().onChange {
            if (nodes.size == 3) nodes[2] = content
            else nodes.add(content)
        }
        contentList.centerProperty().onChange {
            if (nodes.size == 3) nodes.removeAt(2)
        }
        nodes.onChange {
            val w = 800.0 + (nodes.size - 2) * 750
            super.currentStage?.minWidth = w
            super.currentStage?.width = w
        }
    }

    private fun viewWrapper(view: ObservableObjectValue<Parent>, minW: Double? = null, maxW: Double? = null) =
        borderpane {
            centerProperty().bind(view)
            addClass(subview)
            minW?.let { minWidth = it }
            maxW?.let { maxWidth = it }
        }

    private fun sidebar() =
        vbox {
            h1(currentCluster.name)
            vbox {
                menuItem("Topics", ICON_TOPICS) { viewModel.setContent(ListTopicView::class) }
                menuItem("Schema Registry", ICON_REGISTRY) { viewModel.setContent(ListSchemaView::class) }
            }
            button("Change cluster") { action { replaceWith<ListClusterView>() } }
            addClass(SideBarStyle.sidebar)
            boundsInParent
        }

    private fun EventTarget.menuItem(name: String, icon: String, onClick: () -> Unit) =
        hbox(spacing = 5.0) {
            imageview(Image(icon)) { fitHeight = 35.0; fitWidth = 35.0; }
            h2(name)
            onMouseClicked = EventHandler { onClick() }
            addClass(SideBarStyle.sidebarItem)
        }

    override fun onDock() {
        super.onDock()
        super.currentStage?.height = 800.0
        super.currentStage?.minWidth = 800.0
        super.currentStage?.isResizable = true
    }

    override fun onError(throwable: Throwable) {
        replaceWith<ListClusterView>()
    }
}
