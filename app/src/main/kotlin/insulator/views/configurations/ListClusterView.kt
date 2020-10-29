package insulator.views.configurations

import insulator.di.factories.ClusterComponentFactory
import insulator.helper.dispatch
import insulator.helper.runOnFXThread
import insulator.kafka.local.LocalKafkaException
import insulator.kafka.model.Cluster
import insulator.ui.common.InsulatorView
import insulator.ui.component.action
import insulator.ui.component.h1
import insulator.ui.component.h2
import insulator.ui.component.settingsButton
import insulator.ui.component.subTitle
import insulator.ui.style.ButtonStyle
import insulator.update.VersionChecker
import insulator.viewmodel.configurations.ListClusterViewModel
import insulator.views.update.UpdateInfoView
import javafx.beans.property.SimpleBooleanProperty
import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.stage.Modality
import tornadofx.action
import tornadofx.addClass
import tornadofx.borderpane
import tornadofx.button
import tornadofx.disableWhen
import tornadofx.hbox
import tornadofx.listview
import tornadofx.progressindicator
import tornadofx.vbox
import tornadofx.visibleWhen
import tornadofx.whenUndocked
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class ListClusterView @Inject constructor(
    override val viewModel: ListClusterViewModel,
    private val clusterComponentFactory: ClusterComponentFactory
) : InsulatorView("Insulator") {

    private val loadingLocalKafka = SimpleBooleanProperty(false)

    override val root = vbox(spacing = 15) {
        h1("Clusters")
        clusterList()
        borderpane {
            left = localKafkaButton()
            right = addNewClusterButton()
        }
    }

    private fun EventTarget.addNewClusterButton() =
        button("Add new cluster") {
            action { clusterComponentFactory.build(Cluster.empty()).clusterView().show() }
            disableWhen(loadingLocalKafka)
            id = "button-add-cluster"
        }

    private fun EventTarget.localKafkaButton() = hbox(alignment = Pos.CENTER_LEFT, spacing = 3) {
        button("Local kafka cluster") {
            action {
                loadingLocalKafka.set(true)
                viewModel.dispatch {
                    startLocalKafka()?.let { openMainView(it) }
                    loadingLocalKafka.set(false)
                }
            }
            disableProperty().bind(loadingLocalKafka)
            addClass(ButtonStyle.blueButton)
            id = "button-local-cluster"
        }
        progressindicator { maxWidth = 15.0; visibleWhen(loadingLocalKafka) }
    }

    private fun EventTarget.clusterList() =
        listview(viewModel.clustersProperty) {
            cellFormat { graphic = buildClusterCell(it) }
            action { openMainView(it) }
            disableWhen(loadingLocalKafka)
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

    private fun openMainView(cluster: Cluster) {
        currentStage?.hide()
        clusterComponentFactory.build(cluster)
            .mainView()
            .also { it.whenUndocked { currentStage?.show() } }
            .openWindow(modality = Modality.WINDOW_MODAL)
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

    override fun onError(throwable: Throwable) = when (throwable) {
        is LocalKafkaException -> Unit
        else -> close()
    }

    companion object {
        // make sure we check the version only once
        private val wasVersionChecked = AtomicBoolean(false)
    }
}
