package insulator.viewmodel.configurations

import insulator.configuration.ConfigurationRepo
import insulator.kafka.model.Cluster
import insulator.kafka.model.SaslConfiguration
import insulator.kafka.model.SchemaRegistryConfiguration
import insulator.kafka.model.SslConfiguration
import javafx.beans.property.Property
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.ItemViewModel
import javax.inject.Inject

class ClusterViewModel @Inject constructor(cluster: ClusterModel, private val configurationRepo: ConfigurationRepo) : ItemViewModel<ClusterModel>(cluster) {


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
    val useScramProperty = bind { item.useScramProperty }

    val schemaRegistryEndpointProperty = bind { item.schemaRegistryEndpointProperty }
    val schemaRegistryUsernameProperty = bind { item.schemaRegistryUsernameProperty }
    val schemaRegistryPasswordProperty = bind { item.schemaRegistryPasswordProperty }

    suspend fun save() = configurationRepo.store(item.toClusterConfig())
    suspend fun delete() = configurationRepo.delete(item.toClusterConfig())
}

class ClusterModel @Inject constructor(cluster: Cluster) {
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
    val useScramProperty = SimpleBooleanProperty(cluster.saslConfiguration.useScram)

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
                sslKeyStorePassword = this.sslKeyStorePasswordProperty.value,
            ),
            useSasl = this.useSaslProperty.value,
            saslConfiguration = SaslConfiguration(
                saslUsername = this.saslUsernameProperty.value,
                saslPassword = this.saslPasswordProperty.value,
                useScram = this.useScramProperty.value,
            ),
            schemaRegistryConfig = SchemaRegistryConfiguration(
                endpoint = this.schemaRegistryEndpointProperty.value,
                username = this.schemaRegistryUsernameProperty.value,
                password = this.schemaRegistryPasswordProperty.value,
            )
        )
}
