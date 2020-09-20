package insulator.views.main

import insulator.di.currentCluster
import insulator.styles.Controls
import insulator.styles.Titles
import insulator.viewmodel.main.MainViewModel
import insulator.views.common.ICON_MENU_SVG
import insulator.views.common.ICON_REGISTRY
import insulator.views.common.ICON_TOPICS
import insulator.views.common.InsulatorView
import insulator.views.configurations.ListClusterView
import insulator.views.main.schemaregistry.ListSchemaView
import insulator.views.main.topic.ListTopicView
import javafx.event.EventHandler
import javafx.event.EventTarget
import javafx.scene.image.Image
import javafx.scene.paint.Color
import tornadofx.* // ktlint-disable no-wildcard-imports

class MainView : InsulatorView<MainViewModel>("Insulator", MainViewModel::class) {

    override val root = stackpane {
        borderpane {
            top = vbox {
                hbox {
                    button {
                        addClass(Controls.iconButton)
                        graphic = SVGIcon(ICON_MENU_SVG, 20.0, Color.BLACK)
                        action { viewModel.toggleSidebar() }
                    }
                    label(viewModel.currentTitle) { addClass(Titles.h1) }
                    addClass(Controls.topBarMenu)
                }
                hbox { addClass(Controls.topBarMenuShadow) }
            }
            centerProperty().bind(viewModel.currentCenter)
        }
        anchorpane {
            vbox {
                menuItem("Topics", ICON_TOPICS, "menu-item-topic") { viewModel.setCurrentView(ListTopicView::class.java) }
                menuItem("Schema Registry", ICON_REGISTRY, "menu-item-schema-registry") { viewModel.setCurrentView(ListSchemaView::class.java) }
                button("Change cluster") { id = "menu-item-change-cluster"; action { viewModel.toggleSidebar(); replaceWith<ListClusterView>() } }

                addClass(Controls.sidebar)
                anchorpaneConstraints { bottomAnchor = 0; leftAnchor = 0; topAnchor = 60.0 }
                boundsInParent
            }

            visibleWhen(viewModel.showSidebar)
            isPickOnBounds = false
            padding = insets(-15.0, 0.0)
        }
        addClass(Controls.view)
    }

    private fun EventTarget.menuItem(name: String, icon: String, id: String, onClick: () -> Unit) =
        hbox(spacing = 5.0) {
            this.id = id
            imageview(Image(icon)) { fitHeight = 35.0; fitWidth = 35.0; }
            label(name) { addClass(Titles.h2) }
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
