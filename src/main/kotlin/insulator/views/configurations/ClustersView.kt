package insulator.views.configurations

import insulator.views.common.settingsButton
import insulator.views.common.title
import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import tornadofx.*
import tornadofx.label
import tornadofx.vbox
import javax.inject.Singleton

@Singleton
class ClustersView : View("Insulator") {

    override val root = vbox(alignment = Pos.CENTER, spacing = 15) {
        padding = Insets(10.0)
        title("Clusters")

        listview(FXCollections.observableArrayList("Alpha", "Beta", "Gamma", "Delta")) {
            cellFormat { clusterName ->
                graphic = borderpane {
                    center = vbox {
                        title(clusterName)
                        label("http://$clusterName")
                    }
                    right = vbox(alignment = Pos.CENTER) {
                        settingsButton {
                            onMouseClicked = EventHandler { replaceWith(ConfigurationView(clusterName)) }
                        }
                    }
                    maxHeight = 50.0
                }
            }
        }
        button {
            text = "Add new cluster"
        }
    }
}