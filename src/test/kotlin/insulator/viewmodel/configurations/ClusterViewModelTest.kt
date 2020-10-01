package insulator.viewmodel.configurations

import arrow.core.right
import helper.cleanupDi
import helper.configureDi
import insulator.lib.configuration.ConfigurationRepo
import insulator.lib.configuration.model.Cluster
import insulator.lib.configuration.model.SaslConfiguration
import insulator.lib.configuration.model.SchemaRegistryConfiguration
import insulator.lib.configuration.model.SslConfiguration
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID

class ClusterViewModelTest : FunSpec({

    test("Save all info") {
        // arrange
        val mockConfigurationRepo = mockk<ConfigurationRepo> {
            every { store(any()) } returns Unit.right()
        }
        configureDi(ConfigurationRepo::class to mockConfigurationRepo)
        val sampleCluster = Cluster(
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
            useSasl = false,
            saslConfiguration = SaslConfiguration(),
            schemaRegistryConfig = SchemaRegistryConfiguration()
        )
        val sut = ClusterViewModel(ClusterModel(sampleCluster))
        // act
        sut.save()
        // assert
        verify(exactly = 1) { mockConfigurationRepo.store(sampleCluster) }
    }

    test("Save plaintext cluster") {
        // arrange
        val mockConfigurationRepo = mockk<ConfigurationRepo> {
            every { store(any()) } returns Unit.right()
        }
        configureDi(ConfigurationRepo::class to mockConfigurationRepo)
        val sampleCluster = Cluster(
            guid = UUID.randomUUID(),
            name = "clusterName",
            endpoint = "endpoint",
            useSSL = false,
            sslConfiguration = SslConfiguration(),
            useSasl = false,
            saslConfiguration = SaslConfiguration(),
            schemaRegistryConfig = SchemaRegistryConfiguration()
        )
        val sut = ClusterViewModel(ClusterModel(sampleCluster))
        // act
        sut.save()
        // assert
        verify(exactly = 1) { mockConfigurationRepo.store(sampleCluster) }
    }

    test("Save SASL cluster") {
        // arrange
        val mockConfigurationRepo = mockk<ConfigurationRepo> {
            every { store(any()) } returns Unit.right()
        }
        configureDi(ConfigurationRepo::class to mockConfigurationRepo)
        val sampleCluster = Cluster(
            guid = UUID.randomUUID(),
            name = "clusterName",
            endpoint = "endpoint",
            useSSL = false,
            sslConfiguration = SslConfiguration(),
            useSasl = true,
            saslConfiguration = SaslConfiguration(
                saslUsername = "username",
                saslPassword = "password"
            ),
            schemaRegistryConfig = SchemaRegistryConfiguration()
        )
        val sut = ClusterViewModel(ClusterModel(sampleCluster))
        // act
        sut.save()
        // assert
        verify(exactly = 1) { mockConfigurationRepo.store(sampleCluster) }
    }

    test("Delete a cluster from the configuration repo") {
        // arrange
        val mockConfigurationRepo = mockk<ConfigurationRepo> {
            every { store(any()) } returns Unit.right()
            every { delete(any()) } returns Unit.right()
        }
        configureDi(ConfigurationRepo::class to mockConfigurationRepo)
        val mockCluster = Cluster.empty()
        val sut = ClusterViewModel(ClusterModel(mockCluster))
        // act
        sut.delete()
        // assert
        verify(exactly = 1) { mockConfigurationRepo.delete(mockCluster) }
    }

    test("Empty cluster values in model") {
        // arrange
        val mockConfigurationRepo = mockk<ConfigurationRepo> {
            every { store(any()) } returns Unit.right()
            every { delete(any()) } returns Unit.right()
        }
        configureDi(ConfigurationRepo::class to mockConfigurationRepo)
        val mockCluster = Cluster.empty()

        // act
        val sut = ClusterViewModel(ClusterModel(mockCluster))
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

    test("Create a new cluster values in model") {
        // arrange
        val mockConfigurationRepo = mockk<ConfigurationRepo> {
            every { store(any()) } returns Unit.right()
            every { delete(any()) } returns Unit.right()
        }
        configureDi(ConfigurationRepo::class to mockConfigurationRepo)
        val mockCluster = Cluster.empty()

        // act
        val sut = ClusterViewModel(ClusterModel(mockCluster))
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

    afterTest { cleanupDi() }
})
