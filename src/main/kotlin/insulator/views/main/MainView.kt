package insulator.views.main

import insulator.di.currentCluster
import insulator.styles.Controls
import insulator.ui.component.appBar
import insulator.ui.component.burgerButton
import insulator.ui.component.h1
import insulator.ui.component.h2
import insulator.viewmodel.main.MainViewModel
import insulator.views.common.ICON_REGISTRY
import insulator.views.common.ICON_TOPICS
import insulator.views.common.InsulatorView
import insulator.views.configurations.ListClusterView
import insulator.views.main.schemaregistry.ListSchemaView
import insulator.views.main.topic.ListTopicView
import javafx.event.EventHandler
import javafx.event.EventTarget
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
        anchorpane {
            vbox {
                menuItem("Topics", ICON_TOPICS) { viewModel.setCurrentView(ListTopicView::class) }
                menuItem("Schema Registry", ICON_REGISTRY) { viewModel.setCurrentView(ListSchemaView::class) }
                button("Change cluster") { action { viewModel.toggleSidebar(); replaceWith<ListClusterView>() } }

                addClass(Controls.sidebar)
                anchorpaneConstraints { bottomAnchor = 0; leftAnchor = 0; topAnchor = 60.0 }
                boundsInParent
            }

            visibleWhen(viewModel.showSidebar)
            isPickOnBounds = false
            padding = insets(-15.0, 0.0)
        }
    }

    private fun EventTarget.menuItem(name: String, icon: String, onClick: () -> Unit) =
        hbox(spacing = 5.0) {
            imageview(Image(icon)) { fitHeight = 35.0; fitWidth = 35.0; }
            h2(name)
            onMouseClicked = EventHandler { onClick(); viewModel.toggleSidebar() }
            addClass(Controls.sidebarItem)
        }

    override fun onDock() {
        super.onDock()
        super.currentStage?.width = 800.0
        super.currentStage?.height = 800.0
        title = currentCluster.name
    }

    override fun onError(throwable: Throwable) {
        replaceWith<ListClusterView>()
    }
}
