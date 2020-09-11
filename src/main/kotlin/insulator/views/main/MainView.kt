package insulator.views.main

import insulator.di.currentCluster
import insulator.styles.Controls
import insulator.styles.Titles
import insulator.views.common.ICON_MENU_SVG
import insulator.views.common.ICON_REGISTRY
import insulator.views.common.ICON_TOPICS
import insulator.views.configurations.ListClusterView
import insulator.views.main.schemaregistry.ListSchemaView
import insulator.views.main.topic.ListTopicView
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.event.EventTarget
import javafx.scene.control.Alert
import javafx.scene.image.Image
import javafx.scene.paint.Color
import tornadofx.* // ktlint-disable no-wildcard-imports
import java.util.concurrent.Callable

class MainView : View("Insulator") {

    private val showSidebar = SimpleBooleanProperty(false)
    private val currentViewProperty = SimpleObjectProperty<View>().also { it.value = find<ListTopicView>() }
    private val currentCenter = Bindings.createObjectBinding(Callable { currentViewProperty.value.root }, currentViewProperty)
    private val currentTitle = Bindings.createStringBinding(Callable { currentViewProperty.value.title }, currentViewProperty)

    override val root = stackpane {
        borderpane {
            top = vbox {
                hbox {
                    button {
                        addClass(Controls.iconButton)
                        graphic = SVGIcon(ICON_MENU_SVG, 20.0, Color.BLACK)
                        action { showSidebar.set(!showSidebar.value) }
                    }
                    label(currentTitle) { addClass(Titles.h1) }
                    addClass(Controls.topBarMenu)
                }
                hbox { addClass(Controls.topBarMenuShadow) }
            }
            centerProperty().bind(currentCenter)
        }
        anchorpane {
            visibleWhen(showSidebar)
            isPickOnBounds = false
            padding = insets(-15.0, 0.0)
            vbox {
                addClass(Controls.sidebar)
                anchorpaneConstraints { bottomAnchor = 0; leftAnchor = 0; topAnchor = 60.0 }
                boundsInParent

                menuItem("Topics", ICON_TOPICS) { currentViewProperty.set(find<ListTopicView>()) }
                menuItem("Schema Registry", ICON_REGISTRY) {
                    if (currentCluster.isSchemaRegistryConfigured()) currentViewProperty.set(find<ListSchemaView>())
                    else alert(Alert.AlertType.WARNING, "Schema registry configuration not found", owner = currentWindow)
                }
                button("Change cluster") { action { replaceWith<ListClusterView>() } }
            }
        }
        addClass(Controls.view)
    }

    private fun EventTarget.menuItem(name: String, icon: String, onClick: () -> Unit) =
        hbox(spacing = 5.0) {
            imageview(Image(icon)) { fitHeight = 35.0; fitWidth = 35.0; }
            label(name) { addClass(Titles.h2) }
            onMouseClicked = EventHandler { onClick(); showSidebar.set(false) }
            addClass(Controls.sidebarItem)
        }

    override fun onDock() {
        super.onDock()
        super.currentStage?.width = 800.0
        super.currentStage?.height = 800.0
        title = currentCluster.name
    }
}
