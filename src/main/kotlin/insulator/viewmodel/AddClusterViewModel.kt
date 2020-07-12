package insulator.viewmodel

import insulator.configuration.ConfigurationRepo
import insulator.model.Cluster
import javafx.beans.property.Property
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue
import tornadofx.*

class AddClusterViewModel(private val configurationRepo: ConfigurationRepo) {

    val validProperty = SimpleBooleanProperty(false)
    val sslTruststorePasswordProperty = SimpleStringProperty()
    val sslTruststoreLocationProperty = SimpleStringProperty()
    val sslKeystoreLocationProperty = SimpleStringProperty()
    val sslKeyStorePasswordProperty = SimpleStringProperty()
    val useSSLProperty: Property<Boolean> = SimpleBooleanProperty(false)
    val endpointProperty = SimpleStringProperty().also { it.onChange { updateIsValid() } }
    val clusterNameProperty = SimpleStringProperty().also { it.onChange { updateIsValid() } }

    fun save() {
        configurationRepo.store(
                Cluster(clusterNameProperty.value,
                        endpointProperty.value,
                        useSSLProperty.value,
                        sslTruststoreLocationProperty.value,
                        sslTruststorePasswordProperty.value,
                        sslKeystoreLocationProperty.value,
                        sslKeyStorePasswordProperty.value))
                .mapLeft { println("Unable to store the new configuration $it") }
    }

    private fun updateIsValid(): Unit =
            validProperty.set(!endpointProperty.value.isNullOrEmpty() && !clusterNameProperty.value.isNullOrEmpty())
}
