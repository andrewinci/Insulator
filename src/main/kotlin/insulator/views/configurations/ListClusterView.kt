package insulator.views.configurations

import insulator.di.dagger.components.ClusterComponent
import insulator.di.dagger.factories.Factory
import insulator.lib.configuration.model.Cluster
import insulator.lib.helpers.dispatch
import insulator.lib.helpers.runOnFXThread
import insulator.lib.update.VersionChecker
import insulator.ui.common.InsulatorView
import insulator.ui.component.action
import insulator.ui.component.h1
import insulator.ui.component.h2
import insulator.ui.component.settingsButton
import insulator.ui.component.subTitle
import insulator.viewmodel.configurations.ListClusterViewModel
import insulator.views.update.UpdateInfoView
import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.stage.Modality
import javafx.stage.StageStyle
import tornadofx.* // ktlint-disable no-wildcard-imports
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class ListClusterView @Inject constructor(
    override val viewModel: ListClusterViewModel,
    val factory: Factory<Cluster, ClusterComponent>
) : InsulatorView("Insulator") {

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
                factory.build(Cluster.empty())
                    .getClusterView()
                    .openWindow(modality = Modality.WINDOW_MODAL, stageStyle = StageStyle.UTILITY)
            }
        }

    private fun EventTarget.clusterList() =
        listview(viewModel.clustersProperty) {
            cellFormat { graphic = buildClusterCell(it) }
            action { cluster ->
                currentStage?.hide()
                factory.build(cluster)
                    .getMainView()
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
                    factory.build(cluster)
                        .getClusterView()
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
