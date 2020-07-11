package insulator.viewmodel

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.right
import insulator.configuration.ConfigurationRepo
import insulator.model.Cluster
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.*


class ConfigurationsViewModel(private val configurationRepo: ConfigurationRepo) : ViewModel() {
    val clusters: Either<Throwable, ObservableList<Cluster>> by lazy {
        configurationRepo.addNewClusterCallback { new -> clusters.map { it.add(new) } }
        configurationRepo.getConfiguration()
                .flatMap { FXCollections.observableArrayList(it.clusters).right() }
    }
}