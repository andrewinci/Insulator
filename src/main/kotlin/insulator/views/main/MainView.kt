package insulator.views.main

import insulator.Styles
import insulator.di.GlobalConfiguration
import insulator.views.common.*
import insulator.views.configurations.ListClusterView
import insulator.views.main.schemaregistry.ListSchemaView
import insulator.views.main.topic.ListTopicView
import javafx.event.EventHandler
import javafx.event.EventTarget
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.Alert
import javafx.scene.image.Image
import javafx.scene.layout.*
import javafx.scene.paint.Color
import tornadofx.*

class MainView : View("Insulator") {

    override val root = borderpane {
        left = vbox {
            label(GlobalConfiguration.currentCluster.name) { addClass(Styles.h1) }
            button("Change") { action { replaceWith<ListClusterView>() } }
            separator(Orientation.HORIZONTAL)
            menuItem("Overview", ICON_HOME) { setCurrentView(find<OverviewView>()) }
            menuItem("Topics", ICON_TOPICS) { setCurrentView(find<ListTopicView>()) }
            menuItem("Schema Registry", ICON_REGISTRY) {
                if (GlobalConfiguration.currentCluster.isSchemaRegistryConfigured()) setCurrentView(find<ListSchemaView>())
                else alert(Alert.AlertType.WARNING, "Schema registry configuration not found")
            }
            addClass(Styles.card, Styles.mainViewLeftPane)
        }
        center = find<OverviewView>().root
        background = Background(BackgroundFill(Color.LIGHTGREY, CornerRadii.EMPTY, Insets.EMPTY))
    }

    private fun setCurrentView(view: View) {
        this.root.center = view.root
    }

    private fun EventTarget.menuItem(name: String, icon: String, onClick: () -> Unit) =
            hbox(spacing = 5.0) {
                imageview(Image(icon, 30.0, 30.0, true, true))
                label(name) { addClass(Styles.h2) }
                onMouseEntered = EventHandler {
                    background = Background(BackgroundFill(Color.LIGHTGREY, CornerRadii(10.0), Insets.EMPTY))
                }
                onMouseExited = EventHandler {
                    background = Background(BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY))
                }
                onMouseClicked = EventHandler { onClick() }
                padding = Insets(0.0, 0.0, 0.0, 5.0)
                minHeight = 50.0
                alignment = Pos.CENTER_LEFT
            }

    override fun onDock() {
        super.onDock()
        setWindowMinSize(800.0, 800.0)
    }
}

