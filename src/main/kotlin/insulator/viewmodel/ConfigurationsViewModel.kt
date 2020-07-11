package insulator.viewmodel

import insulator.configuration.ConfigurationRepo
import insulator.model.Cluster
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.*


class ConfigurationsViewModel(private val configurationRepo: ConfigurationRepo) : ViewModel() {
    val clusters: ObservableList<Cluster> = FXCollections.observableArrayList(emptyList<Cluster>())

    init {
        configurationRepo.addCallback { clusters.add(it) }
        clusters.addAll(configurationRepo.getClusters())
    }
}