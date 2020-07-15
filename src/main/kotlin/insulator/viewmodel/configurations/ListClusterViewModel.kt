package insulator.viewmodel.configurations

import arrow.core.flatMap
import arrow.core.right
import insulator.lib.configuration.ConfigurationRepo
import insulator.lib.configuration.model.Cluster
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.*


class ListClusterViewModel : ViewModel() {

    private val configurationRepo: ConfigurationRepo by di()

    val clustersProperty: ObservableList<Cluster> by lazy {
        configurationRepo.addNewClusterCallback { new -> with(clustersProperty) { clear(); addAll(new.clusters) } }
        configurationRepo.getConfiguration()
                .flatMap { FXCollections.observableArrayList(it.clusters).right() }
                .fold({ throw  it }, { it })
    }
}