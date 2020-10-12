package insulator.viewmodel.configurations

import insulator.di.currentCluster
import insulator.lib.configuration.ConfigurationRepo
import insulator.lib.configuration.model.Cluster
import insulator.viewmodel.common.InsulatorViewModel
import insulator.views.common.StringScope
import insulator.views.configurations.ClusterView
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.stage.Modality
import javafx.stage.StageStyle
import tornadofx.find
import tornadofx.whenUndockedOnce

class ListClusterViewModel : InsulatorViewModel() {

    private val configurationRepo: ConfigurationRepo by di()

    val clustersProperty: ObservableList<Cluster> by lazy {
        configurationRepo.addNewClusterCallback { new -> with(clustersProperty) { clear(); addAll(new.clusters) } }
        FXCollections.observableArrayList(
            configurationRepo.getConfiguration()
                .fold({ error.set(it); emptyList<Cluster>() }, { it.clusters })
        )
    }

    fun openEditClusterWindow(cluster: Cluster = Cluster.empty()) {
        getClusterScope(cluster)
            .let { scope -> find<ClusterView>(scope).also { view -> view.whenUndockedOnce { scope.close() } } }
            .openWindow(modality = Modality.WINDOW_MODAL, stageStyle = StageStyle.UTILITY)
    }

    fun openMainWindow(cluster: Cluster, op: (StringScope) -> Unit) {
        currentCluster = cluster
        op(getClusterScope(cluster))
    }

    private fun getClusterScope(cluster: Cluster) = StringScope("Cluster-${cluster.guid}")
        .withComponent(ClusterViewModel(ClusterModel(cluster)))
}
