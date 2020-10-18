package insulator.views.configurations

import insulator.lib.configuration.model.Cluster
import insulator.lib.helpers.dispatch
import insulator.lib.helpers.runOnFXThread
import insulator.lib.update.VersionChecker
import insulator.ui.common.InsulatorView2
import insulator.ui.common.scope
import insulator.ui.component.action
import insulator.ui.component.h1
import insulator.ui.component.h2
import insulator.ui.component.settingsButton
import insulator.ui.component.subTitle
import insulator.viewmodel.configurations.ClusterModel
import insulator.viewmodel.configurations.ClusterViewModel
import insulator.viewmodel.configurations.ListClusterViewModel
import insulator.views.main.MainView
import insulator.views.update.UpdateInfoView
import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.stage.Modality
import javafx.stage.StageStyle
import tornadofx.* // ktlint-disable no-wildcard-imports
import java.util.concurrent.atomic.AtomicBoolean

class ListClusterView(override val viewModel: ListClusterViewModel) : InsulatorView2<ListClusterViewModel>("Insulator") {

    override val root = vbox(spacing = 15) {
        h1("Clusters")
        clusterList()
        hbox(alignment = Pos.CENTER_RIGHT) {
            addNewClusterButton()
        }
    }

    private fun EventTarget.addNewClusterButton() =
        button("Add new cluster") {
            action {
                getClusterScope()
                    .let { scope -> find<ClusterView>(scope).also { view -> view.whenUndockedOnce { scope.close() } } }
                    .openWindow(modality = Modality.WINDOW_MODAL, stageStyle = StageStyle.UTILITY)
            }
        }

    private fun EventTarget.clusterList() =
        listview(viewModel.clustersProperty) {
            cellFormat { graphic = buildClusterCell(it) }
            action { cluster ->
                currentStage?.hide()
                find<MainView>(getClusterScope(cluster))
                    .also { it.whenUndocked { currentStage?.show() } }
                    .openWindow(modality = Modality.WINDOW_MODAL)
            }
        }

    private fun EventTarget.buildClusterCell(cluster: Cluster) =
        borderpane {
            center = vbox(alignment = Pos.CENTER_LEFT) {
                h2(cluster.name)
                subTitle(cluster.endpoint) { maxWidth = 260.0 }
            }
            right = vbox(alignment = Pos.CENTER_RIGHT) {
                settingsButton {
                    getClusterScope(cluster)
                        .let { scope -> find<ClusterView>(scope).also { view -> view.whenUndockedOnce { scope.close() } } }
                        .openWindow(modality = Modality.WINDOW_MODAL, stageStyle = StageStyle.UTILITY)
                }
            }
            id = "cluster-${cluster.guid}"
        }

    private fun checkVersion() {
        if (wasVersionChecked.compareAndSet(false, true))
            VersionChecker().dispatch {
                getCurrentVersion().map {
                    if (it.latestRelease != null)
                        UpdateInfoView(it.latestRelease)
                            .runOnFXThread { openWindow(modality = Modality.WINDOW_MODAL) }
                }
            }
    }

    private fun getClusterScope(cluster: Cluster = Cluster.empty()) = cluster.scope()
        .withComponent(ClusterViewModel(ClusterModel(cluster)))

    override fun onDock() {
        super.currentStage?.let {
            it.minWidth = 380.0
            it.width = 380.0
            it.minHeight = 500.0
            it.height = 500.0
            it.resizableProperty().value = false
        }
        checkVersion()
        super.onDock()
    }

    override fun onError(throwable: Throwable) {
        // we can't continue without the list of clusters
        close()
    }

    companion object {
        // make sure we check the version only once
        private val wasVersionChecked = AtomicBoolean(false)
    }
}
