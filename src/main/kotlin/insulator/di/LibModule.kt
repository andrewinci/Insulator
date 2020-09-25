package insulator.di

import com.fasterxml.jackson.databind.ObjectMapper
import insulator.lib.configuration.ConfigurationRepo
import insulator.lib.configuration.model.Cluster
import insulator.lib.jsonhelper.JsonFormatter
import insulator.lib.jsonhelper.JsonToAvroConverter
import insulator.lib.kafka.AdminApi
import insulator.lib.kafka.AvroProducer
import insulator.lib.kafka.Consumer
import insulator.lib.kafka.SchemaRegistry
import insulator.lib.kafka.StringProducer
import kotlinx.serialization.json.Json
import org.apache.avro.Schema
import org.koin.core.qualifier.named
import org.koin.dsl.module

val libModule = module {

    // Configurations and helpers
    single { Json {} }
    single { ConfigurationRepo(get()) }
    single { JsonFormatter(get()) }
    single { Schema.Parser() }
    single { ObjectMapper() }
    single { JsonToAvroConverter(get()) }

    scope<Cluster> {
        factory { AdminApi(get(), get()) }
        factory { Consumer(get()) }
        factory { AvroProducer(get(named("avroProducer")), get(), get()) }
        factory { StringProducer(get()) }
        factory { SchemaRegistry(get()) }
    }
}
