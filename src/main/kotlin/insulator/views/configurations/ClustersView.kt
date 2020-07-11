package insulator.views.configurations

import arrow.core.extensions.either.applicativeError.handleError
import arrow.core.flatMap
import arrow.core.handleErrorWith
import arrow.core.right
import insulator.viewmodel.ConfigurationsViewModel
import insulator.views.common.settingsButton
import insulator.views.common.showError
import insulator.views.common.title
import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Alert
import tornadofx.*
import tornadofx.label
import tornadofx.vbox

class ClustersView : View("Insulator") {

    private val viewModel: ConfigurationsViewModel by di()

    override val root = vbox(alignment = Pos.CENTER, spacing = 15) {
        padding = Insets(10.0)
        title("Clusters")
        viewModel.clusters.map {
            listview(it) {
                cellFormat { cluster ->
                    graphic = borderpane {
                        center = vbox {
                            title(cluster.name)
                            label(cluster.url)
                        }
                        right = vbox(alignment = Pos.CENTER) {
                            settingsButton {
                                onMouseClicked = EventHandler { replaceWith(ClusterDetailsView(cluster)) }
                            }
                        }
                        maxHeight = 50.0
                    }
                }
            }
        }.handleError {
            alert(Alert.AlertType.ERROR, it.message ?: "Unable to load the configurations")
            throw it
        }

        button {
            text = "Add new cluster"
            action { find<AddClusterView>().openWindow() }
        }
    }
}