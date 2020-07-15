package insulator.viewmodel

import insulator.configuration.ConfigurationRepo
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*


class ClusterViewModel : ItemViewModel<ClusterModel>(ClusterModel()) {

    private val configurationRepo: ConfigurationRepo by di()

    val nameProperty = bind { item?.nameProperty }
    val endpointProperty = bind { item?.endpointProperty }
    val useSSLProperty = bind { item?.useSSLProperty }
    val sslTruststoreLocationProperty = bind { item?.sslTruststoreLocationProperty }
    val sslTruststorePasswordProperty = bind { item?.sslTruststorePasswordProperty }
    val sslKeystoreLocationProperty = bind { item?.sslKeystoreLocationProperty }
    val sslKeyStorePasswordProperty = bind { item?.sslKeyStorePasswordProperty }

    fun save() {
        configurationRepo.store(this.item.toClusterConfig())
                .mapLeft { println("Unable to store the new configuration $it") }
    }
}

class ClusterModel {
    val nameProperty = SimpleStringProperty()
    var name by nameProperty

    val endpointProperty = SimpleStringProperty()
    var endpoint by endpointProperty

    val useSSLProperty = SimpleBooleanProperty()
    var useSSL by useSSLProperty

    val sslTruststoreLocationProperty = SimpleStringProperty()
    var sslTruststoreLocation by sslTruststoreLocationProperty

    val sslTruststorePasswordProperty = SimpleStringProperty()
    var sslTruststorePassword by sslTruststorePasswordProperty

    val sslKeystoreLocationProperty = SimpleStringProperty()
    var sslKeystoreLocation by sslKeystoreLocationProperty

    val sslKeyStorePasswordProperty = SimpleStringProperty()
    var sslKeyStorePassword by sslKeyStorePasswordProperty
}

fun ClusterModel.toClusterConfig() =
        insulator.configuration.model.Cluster(
                name = this.name,
                endpoint = this.endpoint,
                useSSL = this.useSSL,
                sslTruststoreLocation = this.sslTruststoreLocation,
                sslTruststorePassword = this.sslTruststorePassword,
                sslKeystoreLocation = this.sslKeystoreLocation,
                sslKeyStorePassword = this.sslKeyStorePassword
        )
