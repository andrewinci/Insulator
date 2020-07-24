package insulator.views.main

import insulator.Styles
import insulator.di.GlobalConfiguration
import insulator.views.common.*
import insulator.views.configurations.ListClusterView
import insulator.views.main.schemaregistry.ListSchemaView
import insulator.views.main.topic.ListTopicView
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.event.EventHandler
import javafx.event.EventTarget
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.Alert
import javafx.scene.image.Image
import javafx.scene.paint.Color
import tornadofx.*


class MainView : View("Insulator") {

    private val showSidebar = SimpleBooleanProperty(false)
    private val currentCenter = SimpleObjectProperty<Parent>()
    private val currentTitle = SimpleStringProperty()
    private val currentViewProperty = SimpleObjectProperty<View>().also { currentView ->
        currentView.onChange {
            currentTitle.value = currentView.value.title
            currentCenter.value = currentView.value.root
        }
    }

    init {
        currentViewProperty.value = find<ListTopicView>()
    }

    override val root = stackpane {
        borderpane {
            top = vbox {
                hbox {
                    button {
                        graphic = SVGIcon(ICON_MENU_SVG, 20.0, Color.BLACK)
                        action { showSidebar.set(!showSidebar.value) } }
                    label(currentTitle) { addClass(Styles.h1) }
                    prefHeight = 60.0
                    addClass(Styles.topBarMenu)
                }
                hbox { addClass(Styles.topBarMenuShadow) }
            }
            centerProperty().bind(currentCenter)
        }
        anchorpane {
            visibleWhen(showSidebar)
            isPickOnBounds = false
            padding = insets(-15.0, 0.0)
            vbox {
                alignment = Pos.TOP_CENTER
                addClass(Styles.sidebar)
                anchorpaneConstraints {
                    bottomAnchor = 0
                    leftAnchor = 0
                    topAnchor = 60.0
                }
                boundsInParent
                padding = Insets.EMPTY
                menuItem("Topics", ICON_TOPICS) { currentViewProperty.set(find<ListTopicView>()) }
                menuItem("Schema Registry", ICON_REGISTRY) {
                    if (GlobalConfiguration.currentCluster.isSchemaRegistryConfigured()) currentViewProperty.set(find<ListSchemaView>())
                    else alert(Alert.AlertType.WARNING, "Schema registry configuration not found")
                }
                button("Change cluster") { action { replaceWith<ListClusterView>() } }
            }
        }

    }

    private fun EventTarget.menuItem(name: String, icon: String, onClick: () -> Unit) =
            hbox(spacing = 5.0) {
                imageview(Image(icon)){
                    fitHeight = 30.0;
                    fitWidth = 30.0;
                    insets(0.0, 15.0, 0.0, 0.0)
                }
                label(name) { addClass(Styles.h2) }
                onMouseClicked = EventHandler { onClick(); showSidebar.set(false) }
                addClass(Styles.sidebarItem)
            }

    override fun onDock() {
        super.onDock()
        setWindowMinSize(800.0, 800.0)
    }
}

