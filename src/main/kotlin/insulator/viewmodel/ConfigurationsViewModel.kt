package insulator.viewmodel

import arrow.core.handleErrorWith
import arrow.core.right
import insulator.configuration.ConfigurationRepo
import insulator.model.Cluster
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.*


class ConfigurationsViewModel(private val configurationRepo: ConfigurationRepo) : ViewModel() {
    val clusters: ObservableList<Cluster> = FXCollections.observableArrayList(emptyList<Cluster>())

    init {
        configurationRepo.addNewClusterCallback { clusters.add(it) }
        configurationRepo.getConfiguration()
                .map { clusters.addAll(it.clusters) }
                .handleErrorWith { println("Unable to load the configurations $it").right() }
    }
}