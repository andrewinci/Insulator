package insulator.views.configurations

import insulator.lib.configuration.ConfigurationRepo
import insulator.viewmodel.configurations.ClusterModel
import insulator.viewmodel.configurations.ClusterViewModel
import insulator.viewmodel.configurations.ListClusterViewModel
import insulator.views.common.SizedView
import insulator.views.common.settingsButton
import insulator.views.common.title
import insulator.views.main.MainView
import javafx.event.EventHandler
import javafx.geometry.Pos
import tornadofx.*
import tornadofx.label
import tornadofx.vbox

class ListClusterView : SizedView("Insulator", 350.0, 500.0) {

    private val viewModel: ListClusterViewModel by inject()

    override val root = vbox(alignment = Pos.CENTER, spacing = 15) {
        title("Clusters")
        listview(viewModel.clustersProperty) {
            cellFormat { cluster ->
                graphic = borderpane {
                    center = vbox {
                        title(cluster.name)
                        label(cluster.endpoint)
                    }
                    right = vbox(alignment = Pos.CENTER) {
                        settingsButton {
                            onMouseClicked = EventHandler {
                                val scope = Scope()
                                setInScope(ClusterViewModel(ClusterModel(cluster)), scope)
                                find<ClusterView>(scope).openWindow()
                            }
                        }
                    }
                    maxHeight = 50.0
                }
                onMouseClicked = EventHandler {
                    ConfigurationRepo.currentCluster = cluster
                    replaceWith(find<MainView>())
                }
            }
        }
        button {
            text = "Add new cluster"
            action { find<ClusterView>().openWindow() }
        }
        paddingAll = 10.0
    }

    override fun onDock() {
        super.onDock()
        super.currentStage?.resizableProperty()?.value = false
    }

}