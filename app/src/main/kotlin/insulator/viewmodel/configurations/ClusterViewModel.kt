package insulator.viewmodel.configurations

import insulator.configuration.ConfigurationRepo
import insulator.kafka.model.Cluster
import insulator.kafka.model.SaslConfiguration
import insulator.kafka.model.SchemaRegistryConfiguration
import insulator.kafka.model.SslConfiguration
import javafx.beans.binding.Bindings
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableBooleanValue
import tornadofx.ViewModel
import tornadofx.onChange
import javax.inject.Inject

class ClusterViewModel @Inject constructor(cluster: Cluster, private val configurationRepo: ConfigurationRepo) : ViewModel() {
    private val guid = cluster.guid
    val nameProperty = SimpleStringProperty(cluster.name)
    val endpointProperty = SimpleStringProperty(cluster.endpoint)

    val useSSLProperty = SimpleBooleanProperty(cluster.useSSL)
    val sslTruststoreLocationProperty = SimpleStringProperty(cluster.sslConfiguration.sslTruststoreLocation ?: "")
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

    val isValidProperty: ObservableBooleanValue = Bindings.createBooleanBinding(
        {
            val baseConfig = !nameProperty.value.isNullOrEmpty() && !endpointProperty.value.isNullOrEmpty()
            // if useSSL is true then all the ssl properties need to be not empty
            val sslConfig = (
                !useSSLProperty.value || (
                    !sslTruststoreLocationProperty.value.isNullOrEmpty() &&
                        !sslTruststorePasswordProperty.value.isNullOrEmpty() &&
                        !sslKeystoreLocationProperty.value.isNullOrEmpty() &&
                        !sslKeyStorePasswordProperty.value.isNullOrEmpty()
                    )
                )
            // if useSASL is true then all the sasl related properties need to be not empty
            val saslConfig = (
                !useSaslProperty.value || (
                    !saslUsernameProperty.value.isNullOrEmpty() &&
                        !saslPasswordProperty.value.isNullOrEmpty()
                    )
                )
            // in the schema registry section both user and password needs to be configured or none of them
            val schemaRegistryConfig = !(
                schemaRegistryUsernameProperty.value.isNullOrEmpty().xor(
                    schemaRegistryPasswordProperty.value.isNullOrEmpty()
                )
                )

            baseConfig && sslConfig && saslConfig && schemaRegistryConfig
        },
        nameProperty, endpointProperty,
        useSSLProperty, sslTruststoreLocationProperty, sslTruststorePasswordProperty, sslKeystoreLocationProperty, sslKeyStorePasswordProperty,
        useSaslProperty, saslUsernameProperty, saslPasswordProperty,
        schemaRegistryUsernameProperty, schemaRegistryPasswordProperty
    )

    init {
        useSaslProperty.onEnabledDisable(useSSLProperty)
        useSSLProperty.onEnabledDisable(useSaslProperty)
    }

    suspend fun save() = configurationRepo.store(toClusterConfig())
    suspend fun delete() = configurationRepo.delete(toClusterConfig())

    private fun BooleanProperty.onEnabledDisable(vararg property: BooleanProperty) {
        onChange { base ->
            if (base) property.forEach { it.set(false) }
        }
    }

    private fun toClusterConfig() =
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
