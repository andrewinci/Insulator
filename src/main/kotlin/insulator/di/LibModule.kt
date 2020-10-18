package insulator.di

import com.fasterxml.jackson.databind.ObjectMapper
import insulator.lib.configuration.ConfigurationRepo
import insulator.lib.configuration.model.Cluster
import insulator.lib.jsonhelper.JsonFormatter
import insulator.lib.jsonhelper.avrotojson.AvroToJsonConverter
import insulator.lib.jsonhelper.jsontoavro.FieldParser
import insulator.lib.jsonhelper.jsontoavro.JsonToAvroConverter
import insulator.lib.jsonhelper.jsontoavro.fieldparser.ComplexTypeParsersFactory
import insulator.lib.jsonhelper.jsontoavro.fieldparser.SimpleTypeParsersFactory
import insulator.lib.kafka.AdminApi
import insulator.lib.kafka.AvroProducer
import insulator.lib.kafka.Consumer
import insulator.lib.kafka.SchemaRegistry
import insulator.lib.kafka.StringProducer
import kotlinx.serialization.json.Json
import org.apache.avro.generic.GenericData
import org.koin.core.qualifier.named
import org.koin.dsl.module

val libModule = module {

    // Configurations and helpers
    single { Json {} }
    single { JsonFormatter(get()) }

    // JsonToAvro
    single { ObjectMapper() }
    single { FieldParser(SimpleTypeParsersFactory(), ComplexTypeParsersFactory()) }
    single { JsonToAvroConverter(get(), get(), GenericData.get()) }

    scope<Cluster> {
        factory { AdminApi(get(), get()) }
        factory { Consumer(get(), AvroToJsonConverter(get())) }
        factory { AvroProducer(get(named("avroProducer")), get(), get()) }
        factory { StringProducer(get()) }
        factory { SchemaRegistry(get()) }
    }
}
