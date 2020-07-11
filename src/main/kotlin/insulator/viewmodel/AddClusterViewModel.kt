package insulator.viewmodel

import insulator.configuration.ConfigurationRepo
import insulator.model.Cluster
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import tornadofx.*


class ClusterSettings {
    val clusterNameProperty = SimpleStringProperty()
    val endpointProperty = SimpleStringProperty()
}

class AddClusterViewModel(private val configurationRepo: ConfigurationRepo) : ItemViewModel<ClusterSettings>(ClusterSettings()) {
    val endpoint: StringProperty = bind { item?.endpointProperty }
    val clusterName: StringProperty = bind { item?.clusterNameProperty }
    fun save() {
        configurationRepo.store(Cluster(endpoint.value, clusterName.value))
                .mapLeft { println("Unable to store the new configuration $it") }
    }
}
