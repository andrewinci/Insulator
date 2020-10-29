package insulator.views.configurations

import insulator.di.factories.ClusterComponentFactory
import insulator.helper.dispatch
import insulator.helper.runOnFXThread
import insulator.kafka.model.Cluster
import insulator.ui.common.InsulatorView
import insulator.ui.component.action
import insulator.ui.component.h1
import insulator.ui.component.h2
import insulator.ui.component.settingsButton
import insulator.ui.component.subTitle
import insulator.update.VersionChecker
import insulator.viewmodel.configurations.ListClusterViewModel
import insulator.views.update.UpdateInfoView
import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.stage.Modality
import tornadofx.action
import tornadofx.addClass
import tornadofx.borderpane
import tornadofx.button
import tornadofx.hbox
import tornadofx.listview
import tornadofx.vbox
import tornadofx.whenUndocked
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class ListClusterView @Inject constructor(
    override val viewModel: ListClusterViewModel,
    private val clusterComponentFactory: ClusterComponentFactory
) : InsulatorView("Insulator") {

    override val root = vbox(spacing = 15) {
        h1("Clusters")
        clusterList()
        hbox(alignment = Pos.CENTER_RIGHT) {
            addNewClusterButton()
            addClass("button-bar")
        }
    }

    private fun EventTarget.addNewClusterButton() =
        button("Add new cluster") {
            action {
                clusterComponentFactory.build(Cluster.empty()).clusterView().show()
            }
        }

    private fun EventTarget.clusterList() =
        listview(viewModel.clustersProperty) {
            cellFormat { graphic = buildClusterCell(it) }
            action { cluster ->
                currentStage?.hide()
                clusterComponentFactory.build(cluster)
                    .mainView()
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
                    clusterComponentFactory.build(cluster).clusterView().show()
                }
            }
            id = "cluster-${cluster.guid}"
        }

    private fun checkVersion() {
        if (wasVersionChecked.compareAndSet(false, true))
            VersionChecker().dispatch {
                getCurrentVersion().map {
                    if (it.latestRelease != null)
                        UpdateInfoView(it.latestRelease!!)
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
