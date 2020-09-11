package insulator.di

import insulator.lib.configuration.model.Cluster
import insulator.lib.configuration.model.SchemaRegistryConfiguration
import io.kotest.core.spec.style.StringSpec
import org.koin.dsl.koinApplication
import org.koin.test.check.checkModules
import java.util.UUID

class TestKoin : StringSpec({

    "Test koin modules" {
        currentCluster = Cluster(
            UUID.randomUUID(),
            "test",
            "127.0.0.1:9092",
            schemaRegistryConfig = SchemaRegistryConfiguration("127.0.0.1")
        )
        koinApplication {
            modules(kafkaModule, libModule)
        }.checkModules()
    }
})
