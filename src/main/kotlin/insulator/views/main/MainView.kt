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
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.image.Image
import tornadofx.* // ktlint-disable no-wildcard-imports

class MainView : InsulatorView<MainViewModel>("Insulator", MainViewModel::class) {

    private val nodes: ObservableList<Node> = FXCollections.observableArrayList(
        sidebar(),
        listView()
    )

    override val root = splitpane {
        items.bind(nodes) { it }
    }

    private fun listView() =
        borderpane { centerProperty().bind(viewModel.currentCenter); addClass(subview) }

    private fun sidebar() =
        vbox {
            h1(currentCluster.name)
            vbox {
                menuItem("Topics", ICON_TOPICS) { viewModel.setCurrentView(ListTopicView::class) }
                menuItem("Schema Registry", ICON_REGISTRY) { viewModel.setCurrentView(ListSchemaView::class) }
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
        super.currentStage?.width = 800.0
        super.currentStage?.height = 800.0
        super.currentStage?.isResizable = true
    }

    override fun onError(throwable: Throwable) {
        replaceWith<ListClusterView>()
    }
}
