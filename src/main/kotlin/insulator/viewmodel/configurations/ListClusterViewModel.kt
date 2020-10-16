package insulator.viewmodel.configurations

import insulator.lib.configuration.ConfigurationRepo
import insulator.lib.configuration.model.Cluster
import insulator.lib.helpers.dispatch
import insulator.viewmodel.common.InsulatorViewModel
import javafx.collections.FXCollections
import javafx.collections.ObservableList

class ListClusterViewModel : InsulatorViewModel() {

    private val configurationRepo: ConfigurationRepo by di()

    val clustersProperty: ObservableList<Cluster> = FXCollections.observableArrayList()

    init {
        configurationRepo.addNewClusterCallback { new -> with(clustersProperty) { clear(); addAll(new.clusters) } }
        configurationRepo.dispatch {
            val configurations = getConfiguration()
                .fold({ error.set(it); emptyList() }, { it.clusters })
            clustersProperty.addAll(configurations)
        }
    }
}
