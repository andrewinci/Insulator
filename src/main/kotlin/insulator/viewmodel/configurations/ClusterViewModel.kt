package insulator.viewmodel.configurations

import insulator.lib.configuration.ConfigurationRepo
import insulator.lib.configuration.model.SaslConfiguration
import insulator.lib.configuration.model.SslConfiguration
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

    val useSaslProperty = bind { item?.useSaslProperty }
    val saslUsernameProperty = bind { item?.saslUsernameProperty }
    val saslPasswordProperty = bind { item?.saslPasswordProperty }

    val schemaRegistryEndpointProperty = bind { item?.schemaRegistryEndpointProperty }
    val schemaRegistryUsernameProperty = bind { item?.schemaRegistryUsernameProperty }
    val schemaRegistryPasswordProperty = bind { item?.schemaRegistryPasswordProperty }

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
    val sslTruststoreLocationProperty = SimpleStringProperty(cluster?.sslConfiguration?.sslTruststoreLocation)
    val sslTruststorePasswordProperty = SimpleStringProperty(cluster?.sslConfiguration?.sslTruststorePassword)
    val sslKeystoreLocationProperty = SimpleStringProperty(cluster?.sslConfiguration?.sslKeystoreLocation)
    val sslKeyStorePasswordProperty = SimpleStringProperty(cluster?.sslConfiguration?.sslKeyStorePassword)

    val useSaslProperty = SimpleBooleanProperty(cluster?.useSasl ?: false)
    val saslUsernameProperty = SimpleStringProperty(cluster?.saslConfiguration?.saslUsername)
    val saslPasswordProperty = SimpleStringProperty(cluster?.saslConfiguration?.saslPassword)

    val schemaRegistryEndpointProperty = SimpleStringProperty(cluster?.schemaRegistryConfig?.endpoint)
    val schemaRegistryUsernameProperty = SimpleStringProperty(cluster?.schemaRegistryConfig?.username)
    val schemaRegistryPasswordProperty = SimpleStringProperty(cluster?.schemaRegistryConfig?.password)

    fun toClusterConfig() =
            insulator.lib.configuration.model.Cluster(
                    guid = this.guid,
                    name = this.nameProperty.value,
                    endpoint = this.endpointProperty.value,
                    useSSL = this.useSSLProperty.value,
                    sslConfiguration = SslConfiguration(
                            sslTruststoreLocation = this.sslTruststoreLocationProperty.value,
                            sslTruststorePassword = this.sslTruststorePasswordProperty.value,
                            sslKeystoreLocation = this.sslKeystoreLocationProperty.value,
                            sslKeyStorePassword = this.sslKeyStorePasswordProperty.value
                    ),
                    useSasl = this.useSaslProperty.value,
                    saslConfiguration = SaslConfiguration(
                            saslUsername = this.saslUsernameProperty.value,
                            saslPassword = this.saslPasswordProperty.value
                    )
            )
}
