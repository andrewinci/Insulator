package insulator.di

import insulator.lib.configuration.ConfigurationRepo
import insulator.lib.configuration.model.Cluster
import insulator.lib.jsonhelper.JsonFormatter
import insulator.lib.jsonhelper.JsonToAvroConverter
import insulator.lib.kafka.AdminApi
import insulator.lib.kafka.Consumer
import insulator.lib.kafka.Producer
import insulator.lib.kafka.SchemaRegistry
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val libModule = module {

    // Configurations and helpers
    single { Json {} }
    single { ConfigurationRepo(get()) }
    single { JsonFormatter(get()) }
    single { JsonToAvroConverter() }

    scope<Cluster> {
        factory { AdminApi(get(), get()) }
        factory { Consumer(get()) }
        factory { Producer(get(), get()) }
        factory { SchemaRegistry(get()) }
    }
}
