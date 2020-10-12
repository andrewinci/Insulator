package insulator.viewmodel.configurations

import insulator.lib.configuration.ConfigurationRepo
import insulator.lib.configuration.model.Cluster
import insulator.viewmodel.common.InsulatorViewModel
import javafx.collections.FXCollections
import javafx.collections.ObservableList

class ListClusterViewModel : InsulatorViewModel() {

    private val configurationRepo: ConfigurationRepo by di()

    val clustersProperty: ObservableList<Cluster> by lazy {
        configurationRepo.addNewClusterCallback { new -> with(clustersProperty) { clear(); addAll(new.clusters) } }
        FXCollections.observableArrayList(
            configurationRepo.getConfiguration()
                .fold({ error.set(it); emptyList<Cluster>() }, { it.clusters })
        )
    }
}
