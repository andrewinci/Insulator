package insulator.viewmodel.configurations

import arrow.core.right
import helper.cleanupDi
import helper.cleanupFXFramework
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
        verify (exactly = 1) { mockConfigurationRepo.store(sampleCluster) }
        cleanupDi()
    }

})
