package insulator.viewmodel.configurations

import insulator.lib.configuration.ConfigurationRepo
import insulator.lib.configuration.model.Cluster
import insulator.lib.configuration.model.SaslConfiguration
import insulator.lib.configuration.model.SchemaRegistryConfiguration
import insulator.lib.configuration.model.SslConfiguration
import insulator.lib.helpers.dispatch
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.ItemViewModel

class ClusterViewModel(cluster: ClusterModel = ClusterModel(Cluster.empty())) : ItemViewModel<ClusterModel>(cluster) {

    private val configurationRepo: ConfigurationRepo by di()

    val nameProperty = bind { item.nameProperty }
    val endpointProperty = bind { item.endpointProperty }
    val useSSLProperty = bind { item.useSSLProperty }

    val sslTruststoreLocationProperty = bind { item.sslTruststoreLocationProperty }
    val sslTruststorePasswordProperty = bind { item.sslTruststorePasswordProperty }
    val sslKeystoreLocationProperty = bind { item.sslKeystoreLocationProperty }
    val sslKeyStorePasswordProperty = bind { item.sslKeyStorePasswordProperty }

    val useSaslProperty = bind { item.useSaslProperty }
    val saslUsernameProperty = bind { item.saslUsernameProperty }
    val saslPasswordProperty = bind { item.saslPasswordProperty }

    val schemaRegistryEndpointProperty = bind { item.schemaRegistryEndpointProperty }
    val schemaRegistryUsernameProperty = bind { item.schemaRegistryUsernameProperty }
    val schemaRegistryPasswordProperty = bind { item.schemaRegistryPasswordProperty }

    fun save() = configurationRepo.dispatch { store(item.toClusterConfig()) }
    fun delete() = configurationRepo.dispatch { delete(item.toClusterConfig()) }
}

class ClusterModel(cluster: Cluster) {
    private val guid = cluster.guid
    val nameProperty = SimpleStringProperty(cluster.name)
    val endpointProperty = SimpleStringProperty(cluster.endpoint)

    val useSSLProperty = SimpleBooleanProperty(cluster.useSSL)
    val sslTruststoreLocationProperty = SimpleStringProperty(cluster.sslConfiguration.sslTruststoreLocation)
    val sslTruststorePasswordProperty = SimpleStringProperty(cluster.sslConfiguration.sslTruststorePassword)
    val sslKeystoreLocationProperty = SimpleStringProperty(cluster.sslConfiguration.sslKeystoreLocation)
    val sslKeyStorePasswordProperty = SimpleStringProperty(cluster.sslConfiguration.sslKeyStorePassword)

    val useSaslProperty = SimpleBooleanProperty(cluster.useSasl)
    val saslUsernameProperty = SimpleStringProperty(cluster.saslConfiguration.saslUsername)
    val saslPasswordProperty = SimpleStringProperty(cluster.saslConfiguration.saslPassword)

    val schemaRegistryEndpointProperty = SimpleStringProperty(cluster.schemaRegistryConfig.endpoint)
    val schemaRegistryUsernameProperty = SimpleStringProperty(cluster.schemaRegistryConfig.username)
    val schemaRegistryPasswordProperty = SimpleStringProperty(cluster.schemaRegistryConfig.password)

    fun toClusterConfig() =
        Cluster(
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
            ),
            schemaRegistryConfig = SchemaRegistryConfiguration(
                endpoint = this.schemaRegistryEndpointProperty.value,
                username = this.schemaRegistryUsernameProperty.value,
                password = this.schemaRegistryPasswordProperty.value
            )
        )
}
