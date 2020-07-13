package insulator.views.main

import insulator.views.common.ICON_HOME
import insulator.views.common.ICON_TOPICS
import insulator.views.common.subtitle
import insulator.views.common.title
import insulator.views.main.topic.ListTopicView
import javafx.event.EventHandler
import javafx.event.EventTarget
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.image.Image
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import tornadofx.*

class MainView : View("Insulator") {

    override val root = anchorpane { leftPane;rightPane }

    private val leftPane = anchorpane {
        vbox(alignment = Pos.TOP_CENTER) {
            title("Cluster name") { paddingAll = 5.0 }
            menuItem("Overview", ICON_HOME) { setCurrentView(OverviewView()) }
            menuItem("Topics", ICON_TOPICS) { setCurrentView(ListTopicView()) }

            paddingAll = 5.0;maxWidth = 200.0;minWidth = 200.0
            anchorpaneConstraints { topAnchor = 10; leftAnchor = 10; bottomAnchor = 10; }
            background = Background(BackgroundFill(Paint.valueOf("white"), CornerRadii(10.0), Insets.EMPTY))
        }
    }

    private val rightPane = anchorpane {
        anchorpaneConstraints { topAnchor = 10;rightAnchor = 10;bottomAnchor = 10;leftAnchor = 220.0 }
        background = Background(BackgroundFill(Color.WHITE, CornerRadii(10.0), Insets.EMPTY))
    }

    private fun setCurrentView(view: Node) = rightPane.children.clear().also { rightPane.children.add(view) }

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

    override fun onDock() {
        super.onDock()
        super.currentStage?.width = 800.0
        super.currentStage?.height = 800.0
        if (rightPane.children.isEmpty()) setCurrentView(OverviewView())
    }

}

