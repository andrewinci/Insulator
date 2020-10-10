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
import insulator.views.style.MainViewStyle
import insulator.views.style.SideBarStyle
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.event.EventTarget
import javafx.geometry.Side
import javafx.scene.Parent
import javafx.scene.image.Image
import tornadofx.* // ktlint-disable no-wildcard-imports

class MainView : InsulatorView<MainViewModel>("Insulator", MainViewModel::class) {

    private val contentList = borderpane {
        centerProperty().bind(viewModel.contentList)
        addClass(MainViewStyle.contentList)
        minWidth = 500.0
        maxWidth = 500.0
    }

    private val content = tabpane {
        viewModel.contentTabs = tabs
        minWidth = 750.0
        side = Side.BOTTOM
        addClass(MainViewStyle.content)
    }

    private val nodes: ObservableList<Parent> = FXCollections.observableArrayList(
        sidebar(),
        contentList
    )

    override val root = splitpane { items.bind(nodes) { it } }

    init {
        viewModel.contentTabs.onChange {
            when {
                viewModel.contentTabs.size == 0 -> nodes.removeAt(2)
                nodes.size <= 2 -> nodes.add(content)
                else -> nodes[2] = content
            }
        }
        nodes.onChange { setSize() }
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

    private fun setSize(){
        val w = 800.0 + (nodes.size - 2) * 750
        super.currentStage?.minWidth = w
        super.currentStage?.width = w
        super.currentStage?.minHeight = 800.0
    }

    override fun onDock() {
        super.onDock()
        setSize()
        super.currentStage?.isResizable = true
    }

    override fun onError(throwable: Throwable) {
        replaceWith<ListClusterView>()
    }
}
