package insulator.views.main

import insulator.lib.configuration.ConfigurationRepo
import insulator.views.common.*
import insulator.views.main.topic.ListTopicView
import javafx.event.EventHandler
import javafx.event.EventTarget
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.image.Image
import javafx.scene.layout.*
import javafx.scene.paint.Color
import tornadofx.*

class MainView : SizedView("Insulator", 800.0, 800.0) {

    override val root = borderpane {
        left = card(ConfigurationRepo.currentCluster.name, 200.0) {
            separator(Orientation.HORIZONTAL)
            menuItem("Overview", ICON_HOME) { setCurrentView(OverviewView()) }
            menuItem("Topics", ICON_TOPICS) { setCurrentView(ListTopicView()) }
        }
        center = OverviewView()
        background = Background(BackgroundFill(Color.LIGHTGREY, CornerRadii.EMPTY, Insets.EMPTY))
    }

    private fun setCurrentView(view: Node) {
        this.root.center = view
    }

    private fun EventTarget.menuItem(name: String, icon: String, onClick: () -> Unit) =
            borderpane {
                center = subtitle(name, Color.ORANGERED) {
                    padding = Insets(0.0, 0.0, 0.0, 10.0)
                }
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
}

