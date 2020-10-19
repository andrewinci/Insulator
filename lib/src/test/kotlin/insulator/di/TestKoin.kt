package insulator.di.insulator.di

import insulator.di.kafkaModule
import insulator.di.libModule
import insulator.di.setGlobalCluster
import insulator.lib.configuration.model.Cluster
import insulator.lib.configuration.model.SaslConfiguration
import insulator.lib.configuration.model.SchemaRegistryConfiguration
import insulator.lib.configuration.model.SslConfiguration
import io.kotest.core.spec.style.StringSpec
import org.koin.dsl.koinApplication
import org.koin.test.check.checkModules
import java.nio.file.Paths
import java.util.UUID

class TestKoin : StringSpec({

    "Test koin modules" {
        // arrange
        setGlobalCluster(
            Cluster(
                UUID.randomUUID(),
                "test",
                "127.0.0.1:9092",
                schemaRegistryConfig = SchemaRegistryConfiguration("127.0.0.1")
            )
        )
        // test
        koinApplication {
            modules(kafkaModule, libModule)
        }.checkModules()
    }

    "Test koin modules (SSL)" {
        // arrange
        val resourceDirectory = Paths.get("src", "test", "resources", "example.p12")
        val absolutePath = resourceDirectory.toFile().absolutePath
        setGlobalCluster(
            Cluster(
                guid = UUID.randomUUID(),
                name = "test",
                endpoint = "127.0.0.1:9092",
                useSSL = true,
                sslConfiguration = SslConfiguration(
                    sslTruststoreLocation = absolutePath,
                    sslTruststorePassword = "1234",
                    sslKeystoreLocation = absolutePath,
                    sslKeyStorePassword = "1234"
                ),
                schemaRegistryConfig = SchemaRegistryConfiguration("127.0.0.1")
            )
        )
        // test
        koinApplication {
            modules(kafkaModule, libModule)
        }.checkModules()
    }

    "Test koin modules (SASL)" {
        // arrange
        setGlobalCluster(
            Cluster(
                guid = UUID.randomUUID(),
                name = "test",
                endpoint = "127.0.0.1:9092",
                useSasl = true,
                saslConfiguration = SaslConfiguration(
                    saslUsername = "",
                    saslPassword = ""
                ),
                schemaRegistryConfig = SchemaRegistryConfiguration("127.0.0.1")
            )
        )
        // test
        koinApplication {
            modules(kafkaModule, libModule)
        }.checkModules()
    }
})
