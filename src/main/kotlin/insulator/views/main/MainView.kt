package insulator.views.main

import insulator.di.currentCluster
import insulator.views.component.h2
import insulator.views.style.SideBarStyle
import insulator.viewmodel.main.MainViewModel
import insulator.views.common.ICON_REGISTRY
import insulator.views.common.ICON_TOPICS
import insulator.views.common.InsulatorView
import insulator.views.component.appBar
import insulator.views.component.burgerButton
import insulator.views.component.h1
import insulator.views.configurations.ListClusterView
import insulator.views.main.schemaregistry.ListSchemaView
import insulator.views.main.topic.ListTopicView
import javafx.event.EventHandler
import javafx.event.EventTarget
import javafx.geometry.Orientation
import javafx.scene.image.Image
import tornadofx.* // ktlint-disable no-wildcard-imports

class MainView : InsulatorView<MainViewModel>("Insulator", MainViewModel::class) {

    override val root = stackpane {
        borderpane {
            top = appBar {
                hbox {
                    burgerButton { viewModel.toggleSidebar() }
                    h1(viewModel.currentTitle)
                }
            }
            centerProperty().bind(viewModel.currentCenter)
        }
        sidebar()
    }

    private fun EventTarget.sidebar() =
        anchorpane {
            vbox {
                menuItem("Topics", ICON_TOPICS) { viewModel.setCurrentView(ListTopicView::class) }
                menuItem("Schema Registry", ICON_REGISTRY) { viewModel.setCurrentView(ListSchemaView::class) }
                button("Change cluster") { action { viewModel.toggleSidebar(); replaceWith<ListClusterView>() } }

                anchorpaneConstraints { bottomAnchor = 0; leftAnchor = -13; topAnchor = 67.0 }
                addClass(SideBarStyle.sidebar)
                boundsInParent
            }

            visibleWhen(viewModel.showSidebar)
            isPickOnBounds = false
        }

    private fun EventTarget.menuItem(name: String, icon: String, onClick: () -> Unit) =
        hbox(spacing = 5.0) {
            imageview(Image(icon)) { fitHeight = 35.0; fitWidth = 35.0; }
            h2(name)
            onMouseClicked = EventHandler { onClick(); viewModel.toggleSidebar() }
            addClass(SideBarStyle.sidebarItem)
        }

    override fun onDock() {
        super.onDock()
        super.currentStage?.width = 800.0
        super.currentStage?.height = 800.0
        super.currentStage?.isResizable = true
        title = currentCluster.name
    }

    override fun onError(throwable: Throwable) {
        replaceWith<ListClusterView>()
    }
}
