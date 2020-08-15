package insulator.di

import insulator.lib.configuration.ConfigurationRepo
import insulator.lib.configuration.model.Cluster
import insulator.lib.jsonhelper.JsonFormatter
import insulator.lib.kafka.AdminApi
import insulator.lib.kafka.Consumer
import insulator.lib.kafka.SchemaRegistry
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.koin.dsl.module

val libModule = module {

    // Configurations
    single { Json(JsonConfiguration.Stable) }
    single { ConfigurationRepo(get()) }
    single { JsonFormatter(get()) }

    scope<Cluster> {
        factory { AdminApi(get(), get()) }
        factory { Consumer(get()) }
        factory { SchemaRegistry(get()) }
    }
}
