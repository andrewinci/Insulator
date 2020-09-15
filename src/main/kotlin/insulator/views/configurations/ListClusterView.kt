package insulator.views.configurations

import insulator.di.currentCluster
import insulator.lib.configuration.model.Cluster
import insulator.styles.Controls
import insulator.styles.Titles
import insulator.viewmodel.configurations.ClusterModel
import insulator.viewmodel.configurations.ClusterViewModel
import insulator.viewmodel.configurations.ListClusterViewModel
import insulator.views.common.ICON_SETTINGS_SVG
import insulator.views.common.InsulatorView
import insulator.views.common.StringScope
import insulator.views.main.MainView
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.layout.BorderPane
import javafx.stage.Modality
import javafx.stage.StageStyle
import tornadofx.* // ktlint-disable no-wildcard-imports

class ListClusterView : InsulatorView<ListClusterViewModel>("Insulator", ListClusterViewModel::class) {

    override val root = vbox(spacing = 15) {
        label("Clusters") { addClass(Titles.h1) }
        listview(viewModel.clustersProperty) {
            cellFormat { cluster ->
                graphic = buildClusterCell(cluster)
                onMouseClicked = EventHandler {
                    currentCluster = cluster
                    StringScope("Cluster-${cluster.guid}")
                        .withComponent(ClusterViewModel(ClusterModel(cluster)))
                        .let { replaceWith(find<MainView>(it)) }
                }
            }
        }
        hbox(alignment = Pos.CENTER_RIGHT) {
            button {
                alignment = Pos.CENTER_RIGHT
                text = "Add new cluster"
                action {
                    val clusterScope = StringScope("CreateNewCluster")
                        .withComponent(ClusterViewModel(ClusterModel(Cluster.empty())))
                    find<ClusterView>(clusterScope).also { it.whenUndockedOnce { clusterScope.close() } }
                        .openWindow(StageStyle.UTILITY, Modality.WINDOW_MODAL)
                }
            }
        }
        addClass(Controls.view)
    }

    private fun buildClusterCell(cluster: Cluster): BorderPane {
        return borderpane {
            maxHeight = 80.0
            center = vbox(alignment = Pos.CENTER_LEFT) {
                label(cluster.name) { addClass(Titles.h2) }
                label(cluster.endpoint) { addClass(Titles.h3) }
            }
            right = vbox(alignment = Pos.CENTER) {
                button {
                    addClass(Controls.iconButton)
                    graphic = SVGIcon(ICON_SETTINGS_SVG, 18)
                    onMouseClicked = EventHandler {
                        StringScope("Cluster-${cluster.guid}")
                            .withComponent(ClusterViewModel(ClusterModel(cluster)))
                            .let { find<ClusterView>(it).openWindow(StageStyle.UTILITY, Modality.WINDOW_MODAL) }
                    }
                }
            }
        }
    }

    override fun onDock() {
        super.onDock()
        super.currentStage?.width = 380.0
        super.currentStage?.height = 500.0
        super.currentStage?.resizableProperty()?.value = false
    }

    override fun onError(throwable: Throwable) {
        // we can't continue without the list of clusters
        close()
    }
}
