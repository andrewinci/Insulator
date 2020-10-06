package insulator.views.configurations

import insulator.lib.configuration.model.Cluster
import insulator.lib.update.VersionChecker
import insulator.ui.component.h1
import insulator.ui.component.h2
import insulator.ui.component.settingsButton
import insulator.ui.component.subTitle
import insulator.viewmodel.configurations.ListClusterViewModel
import insulator.views.common.InsulatorView
import insulator.views.common.customOpenWindow
import insulator.views.main.MainView
import insulator.views.update.UpdateInfoView
import javafx.event.EventHandler
import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.stage.Modality
import tornadofx.* // ktlint-disable no-wildcard-imports
import java.util.concurrent.atomic.AtomicBoolean

class ListClusterView : InsulatorView<ListClusterViewModel>("Insulator", ListClusterViewModel::class) {

    override val root = vbox(spacing = 15) {
        h1("Clusters")
        clusterList()
        hbox(alignment = Pos.CENTER_RIGHT) {
            addNewClusterButton()
        }
    }

    private fun EventTarget.addNewClusterButton() = button {
        text = "Add new cluster"
        action { viewModel.onAddNewClusterClick() }
    }

    private fun EventTarget.clusterList() = listview(viewModel.clustersProperty) {
        cellFormat { cluster ->
            graphic = buildClusterCell(cluster)
            onMouseClicked = EventHandler {
                viewModel.onClusterSelected(cluster) { replaceWith(find<MainView>(it)) }
            }
        }
    }

    private fun EventTarget.buildClusterCell(cluster: Cluster) =
        borderpane {
            center = vbox(alignment = Pos.CENTER_LEFT) {
                h2(cluster.name)
                subTitle(cluster.endpoint) { maxWidth = 260.0 }
            }
            right = settingsButton { viewModel.onSettingsButtonClick(cluster) }
            id = "cluster-${cluster.guid}"
        }

    private fun checkVersion() {
        if (wasVersionChecked.compareAndSet(false, true))
            VersionChecker().getCurrentVersion().map {
                if (it.latestRelease != null)
                    UpdateInfoView(it.latestRelease).customOpenWindow(modality = Modality.WINDOW_MODAL)
            }
    }

    override fun onDock() {
        super.onDock()
        super.currentStage?.width = 380.0
        super.currentStage?.height = 500.0
        super.currentStage?.resizableProperty()?.value = false
        checkVersion()
    }

    override fun onError(throwable: Throwable) {
        // we can't continue without the list of clusters
        close()
    }

    companion object {
        private val wasVersionChecked = AtomicBoolean(false)
    }
}
