package insulator.viewmodel

import insulator.configuration.ConfigurationRepo
import insulator.model.Cluster
import javafx.beans.property.Property
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.beans.value.ObservableValue
import tornadofx.*


class ClusterSettings {
    val sslTruststorePassword = SimpleStringProperty()
    val sslTruststoreLocation = SimpleStringProperty()
    val sslKeystoreLocation = SimpleStringProperty()
    val sslKeyStorePassword = SimpleStringProperty()
    val useSSL = SimpleBooleanProperty(false)
    val clusterNameProperty = SimpleStringProperty()
    val endpointProperty = SimpleStringProperty()
}

class AddClusterViewModel(private val configurationRepo: ConfigurationRepo) : ItemViewModel<ClusterSettings>(ClusterSettings()) {

    val sslTruststorePassword: StringProperty = bind { item?.sslTruststorePassword }
    val sslTruststoreLocation: StringProperty = bind { item?.sslTruststoreLocation }
    val sslKeystoreLocation: StringProperty = bind { item?.sslKeystoreLocation }
    val sslKeyStorePassword: StringProperty = bind { item?.sslKeyStorePassword }
    val useSSL: Property<Boolean> = bind { item?.useSSL }
    val endpoint: StringProperty = bind { item?.endpointProperty }
    val clusterName: StringProperty = bind { item?.clusterNameProperty }

    fun save() {
        configurationRepo.store(
                Cluster(clusterName.value,
                        endpoint.value,
                        useSSL.value,
                        sslTruststoreLocation.value,
                        sslTruststorePassword.value,
                        sslKeystoreLocation.value,
                        sslKeyStorePassword.value))
                .mapLeft { println("Unable to store the new configuration $it") }
    }
}
