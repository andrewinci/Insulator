package insulator.views.configurations

import insulator.configuration.ConfigurationRepo
import insulator.configuration.model.Cluster
import insulator.viewmodel.ConfigurationsViewModel
import insulator.views.common.settingsButton
import insulator.views.common.title
import insulator.views.main.MainView
import javafx.event.EventHandler
import javafx.geometry.Pos
import tornadofx.*
import tornadofx.label
import tornadofx.vbox

class ListClusterView : View("Insulator") {

    private val viewModel: ConfigurationsViewModel by inject()

    override val root = vbox(alignment = Pos.CENTER, spacing = 15) {
        title("Clusters")
        listview(viewModel.clustersProperty) {
            cellFormat { cluster ->
                graphic = clusterListViewItem(cluster)
                onMouseClicked = EventHandler {
                    ConfigurationRepo.currentCluster = cluster
                    replaceWith(find<MainView>())
                }
            }
        }
        button {
            text = "Add new cluster"
            action { find<AddClusterView>().openWindow() }
        }
        paddingAll = 10.0
    }


    private fun clusterListViewItem(cluster: Cluster) = borderpane {
        center = vbox {
            title(cluster.name)
            label(cluster.endpoint)
        }
        right = vbox(alignment = Pos.CENTER) {
            settingsButton {
                onMouseClicked = EventHandler { replaceWith(find<ClusterDetailsView>()) }//find<CustomerEditor>(mapOf(CustomerEditor::customer to customer))
            }
        }
        maxHeight = 50.0
    }

    override fun onDock() {
        super.onDock()
        super.currentStage?.width = 350.0
        super.currentStage?.height = 500.0
        super.currentStage?.resizableProperty()?.value = false
    }

}