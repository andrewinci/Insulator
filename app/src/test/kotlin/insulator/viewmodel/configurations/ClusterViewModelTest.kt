package insulator.viewmodel.configurations

import arrow.core.right
import helper.FxContext
import insulator.configuration.ConfigurationRepo
import insulator.kafka.model.Cluster
import insulator.kafka.model.SaslConfiguration
import insulator.kafka.model.SchemaRegistryConfiguration
import insulator.kafka.model.SslConfiguration
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.stringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.util.UUID

class ClusterViewModelTest : StringSpec({

    fun mockConfigurationRepo() = mockk<ConfigurationRepo>() {
        coEvery { store(any<Cluster>()) } returns Unit.right()
        coEvery { delete(any()) } returns Unit.right()
    }

    fun testSave(name: String, cluster: Cluster) = stringSpec {
        name {
            FxContext().use {
                // arrange
                val mockConfigurationRepo = mockConfigurationRepo()
                val sut = ClusterViewModel(cluster, mockConfigurationRepo)
                // act
                sut.save()
                // assert
                coVerify(exactly = 1) { mockConfigurationRepo.store(cluster) }
            }
        }
    }

    include(testSave("Save SSL config", sampleCluster.copy(saslConfiguration = SaslConfiguration(), useSasl = false)))
    include(testSave("Save SASL config", sampleCluster.copy(sslConfiguration = SslConfiguration(), useSSL = false)))
    include(
        testSave(
            "Save Plaintext config",
            sampleCluster.copy(
                saslConfiguration = SaslConfiguration(),
                useSasl = false,
                sslConfiguration = SslConfiguration(),
                useSSL = false
            )
        )
    )

    "Delete a cluster from the configuration repo" {
        FxContext().use {
            // arrange
            val mockConfigurationRepo = mockConfigurationRepo()
            val mockCluster = Cluster.empty()
            val sut = ClusterViewModel(mockCluster, mockConfigurationRepo)
            // act
            sut.delete()
            // assert
            coVerify(exactly = 1) { mockConfigurationRepo.delete(mockCluster) }
        }
    }

    "Empty cluster values in model" {
        FxContext().use {
            // arrange
            val mockConfigurationRepo = mockConfigurationRepo()
            val mockCluster = Cluster.empty()
            // act
            val sut = ClusterViewModel(mockCluster, mockConfigurationRepo)
            // assert
            with(sut) {
                nameProperty.value shouldBe ""
                endpointProperty.value shouldBe ""

                useSSLProperty.value shouldBe false
                sslTruststoreLocationProperty.value shouldBe null
                sslTruststorePasswordProperty.value shouldBe null
                sslKeystoreLocationProperty.value shouldBe null
                sslKeyStorePasswordProperty.value shouldBe null

                useSaslProperty.value shouldBe false
                saslUsernameProperty.value shouldBe null
                saslPasswordProperty.value shouldBe null

                schemaRegistryEndpointProperty.value shouldBe null
                schemaRegistryUsernameProperty.value shouldBe null
                schemaRegistryPasswordProperty.value shouldBe null
            }
        }
    }

    "Create a new cluster values in model" {
        FxContext().use {
            // arrange
            val mockConfigurationRepo = mockConfigurationRepo()
            val mockCluster = Cluster.empty()

            // act
            val sut = ClusterViewModel(mockCluster, mockConfigurationRepo)
            with(sut) {
                nameProperty.set("test-name")
                endpointProperty.set("test-endpoint")
                useSSLProperty.set(true)
                sslTruststoreLocationProperty.set("sslTruststoreLocation")
                sslTruststorePasswordProperty.set("sslTruststorePassword")
                sslKeystoreLocationProperty.set("sslKeystoreLocation")
                sslKeyStorePasswordProperty.set("sslKeyStorePassword")
            }

            // assert
            with(sut) {
                nameProperty.value shouldBe "test-name"
                endpointProperty.value shouldBe "test-endpoint"

                useSSLProperty.value shouldBe true
                sslTruststoreLocationProperty.value shouldBe "sslTruststoreLocation"
                sslTruststorePasswordProperty.value shouldBe "sslTruststorePassword"
                sslKeystoreLocationProperty.value shouldBe "sslKeystoreLocation"
                sslKeyStorePasswordProperty.value shouldBe "sslKeyStorePassword"

                useSaslProperty.value shouldBe false
                saslUsernameProperty.value shouldBe null
                saslPasswordProperty.value shouldBe null

                schemaRegistryEndpointProperty.value shouldBe null
                schemaRegistryUsernameProperty.value shouldBe null
                schemaRegistryPasswordProperty.value shouldBe null
            }
        }
    }

    "viewModel is valid if only cluster name and endpoint are provided" {
        // arrange
        val sut = ClusterViewModel(Cluster.empty(), mockk())
        // act
        sut.nameProperty.set("test")
        sut.endpointProperty.set("test-endpoint")
        // assert
        sut.isValidProperty.value shouldBe true
    }

    "viewModel is not valid if only cluster name is provided" {
        // arrange
        val sut = ClusterViewModel(Cluster.empty(), mockk())
        // act
        sut.nameProperty.set("test")
        // assert
        sut.isValidProperty.value shouldBe false
    }

    "isValid property is false if ssl is selected but not all SSL configurations are set" {
        // arrange
        val sut = ClusterViewModel(Cluster.empty(), mockk())
        // act
        sut.useSSLProperty.set(true)
        sut.sslTruststoreLocationProperty.set("some location")
        // assert
        sut.isValidProperty.value shouldBe false
    }
})

private val sampleCluster = Cluster(
    guid = UUID.randomUUID(),
    name = "clusterName",
    endpoint = "endpoint",
    useSSL = true,
    sslConfiguration = SslConfiguration(
        sslTruststoreLocation = "sslTruststoreLocation",
        sslTruststorePassword = "sslTruststorePassword",
        sslKeystoreLocation = "sslKeystoreLocation",
        sslKeyStorePassword = "sslKeyStorePassword"
    ),
    useSasl = true,
    saslConfiguration = SaslConfiguration(
        saslUsername = "username",
        saslPassword = "password"
    ),
    schemaRegistryConfig = SchemaRegistryConfiguration(
        endpoint = "schema_endpoint",
        username = "schema_username",
        password = "schema_password"
    )
)
