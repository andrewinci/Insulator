package insulator.views.configurations

import insulator.viewmodel.ConfigurationsViewModel
import insulator.views.common.settingsButton
import insulator.views.common.title
import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import tornadofx.*
import tornadofx.label
import tornadofx.vbox

class ClustersView: View("Insulator") {

    private val viewModel: ConfigurationsViewModel by di()

    override val root = vbox(alignment = Pos.CENTER, spacing = 15) {
        padding = Insets(10.0)
        title("Clusters")

        listview(FXCollections.observableArrayList(viewModel.clusters() )) {
            cellFormat { cluster ->
                graphic = borderpane {
                    center = vbox {
                        title(cluster.name)
                        label(cluster.url)
                    }
                    right = vbox(alignment = Pos.CENTER) {
                        settingsButton {
                            onMouseClicked = EventHandler { replaceWith(ConfigurationView(cluster)) }
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