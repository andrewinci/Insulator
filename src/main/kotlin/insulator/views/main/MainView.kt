package insulator.views.main

import insulator.Styles
import insulator.lib.configuration.ConfigurationRepo
import insulator.viewmodel.configurations.ClusterViewModel
import insulator.views.common.*
import insulator.views.main.topic.ListTopicView
import javafx.event.EventHandler
import javafx.event.EventTarget
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.image.Image
import javafx.scene.layout.*
import javafx.scene.paint.Color
import tornadofx.*

class MainView : View("Insulator") {

    override val root = borderpane {
        left = vbox {
            label(ConfigurationRepo.currentCluster.name) { addClass(Styles.h1) }
            separator(Orientation.HORIZONTAL)
            menuItem("Overview", ICON_HOME) { setCurrentView(find<OverviewView>()) }
            menuItem("Topics", ICON_TOPICS) { setCurrentView(find<ListTopicView>()) }
            addClass(Styles.card, Styles.mainMenu)
        }
        center = find<OverviewView>().root
        background = Background(BackgroundFill(Color.LIGHTGREY, CornerRadii.EMPTY, Insets.EMPTY))
    }

    private fun setCurrentView(view: View) {
        this.root.center = view.root
    }

    private fun EventTarget.menuItem(name: String, icon: String, onClick: () -> Unit) =
            borderpane {
                center = label(name) { addClass(Styles.h2) }
                left = vbox {
                    imageview(Image(icon, 30.0, 30.0, true, true))
                    alignment = Pos.CENTER_LEFT
                }
                onMouseEntered = EventHandler {
                    background = Background(BackgroundFill(Color.LIGHTGREY, CornerRadii(10.0), Insets.EMPTY))
                }
                onMouseExited = EventHandler {
                    background = Background(BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY))
                }
                onMouseClicked = EventHandler { onClick() }
                padding = Insets(0.0, 0.0, 0.0, 5.0)
                minHeight = 50.0
            }

    override fun onDock() {
        super.onDock()
        setWindowMinSize(800.0, 800.0)
    }
}

