package insulator.viewmodel.configurations

import insulator.di.currentCluster
import insulator.lib.configuration.ConfigurationRepo
import insulator.lib.configuration.model.Cluster
import insulator.viewmodel.common.InsulatorViewModel
import insulator.views.common.StringScope
import insulator.views.common.customOpenWindow
import insulator.views.configurations.ClusterView
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.stage.Modality
import javafx.stage.StageStyle
import tornadofx.find
import tornadofx.whenUndockedOnce

class ListClusterViewModel : InsulatorViewModel() {

    val selectedCluster = SimpleObjectProperty<Cluster>()
    private val configurationRepo: ConfigurationRepo by di()

    val clustersProperty: ObservableList<Cluster> by lazy {
        configurationRepo.addNewClusterCallback { new -> with(clustersProperty) { clear(); addAll(new.clusters) } }
        FXCollections.observableArrayList(
            configurationRepo.getConfiguration()
                .fold({ error.set(it); emptyList<Cluster>() }, { it.clusters })
        )
    }

    fun onAddNewClusterClick() {
        val clusterScope = StringScope("CreateNewCluster")
            .withComponent(ClusterViewModel(ClusterModel(Cluster.empty())))
        find<ClusterView>(clusterScope).also { it.whenUndockedOnce { clusterScope.close() } }
            .customOpenWindow(modality = Modality.WINDOW_MODAL, stageStyle = StageStyle.UTILITY)
    }

    fun onClusterSelected(cluster: Cluster, op: (StringScope) -> Unit) {
        currentCluster = cluster
        StringScope("Cluster-${cluster.guid}")
            .withComponent(ClusterViewModel(ClusterModel(cluster)))
            .let { op(it) }
    }

    fun onSettingsButtonClick(cluster: Cluster) {
        StringScope("Cluster-${cluster.guid}")
            .withComponent(ClusterViewModel(ClusterModel(cluster)))
            .let { find<ClusterView>(it).customOpenWindow(modality = Modality.WINDOW_MODAL, stageStyle = StageStyle.UTILITY) }
    }
}
