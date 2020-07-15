package insulator.viewmodel.configurations

import insulator.lib.configuration.ConfigurationRepo
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import java.util.*

class ClusterViewModel(cluster: ClusterModel = ClusterModel()) : ItemViewModel<ClusterModel>(cluster) {

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

class ClusterModel(cluster: insulator.lib.configuration.model.Cluster? = null) {
    private val guid = cluster?.guid ?: UUID.randomUUID()
    val nameProperty = SimpleStringProperty(cluster?.name)
    val endpointProperty = SimpleStringProperty(cluster?.endpoint)
    val useSSLProperty = SimpleBooleanProperty(cluster?.useSSL ?: false)
    val sslTruststoreLocationProperty = SimpleStringProperty(cluster?.sslTruststoreLocation)
    val sslTruststorePasswordProperty = SimpleStringProperty(cluster?.sslTruststorePassword)
    val sslKeystoreLocationProperty = SimpleStringProperty(cluster?.sslKeystoreLocation)
    val sslKeyStorePasswordProperty = SimpleStringProperty(cluster?.sslKeyStorePassword)

    fun toClusterConfig() =
            insulator.lib.configuration.model.Cluster(
                    guid = this.guid,
                    name = this.nameProperty.value,
                    endpoint = this.endpointProperty.value,
                    useSSL = this.useSSLProperty.value,
                    sslTruststoreLocation = this.sslTruststoreLocationProperty.value,
                    sslTruststorePassword = this.sslTruststorePasswordProperty.value,
                    sslKeystoreLocation = this.sslKeystoreLocationProperty.value,
                    sslKeyStorePassword = this.sslKeyStorePasswordProperty.value
            )
}
