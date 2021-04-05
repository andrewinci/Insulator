package insulator.di.modules

import com.fasterxml.jackson.databind.ObjectMapper
import dagger.Module
import dagger.Provides
import insulator.configuration.ConfigurationRepo
import insulator.jsonhelper.JsonFormatter
import insulator.jsonhelper.avrotojson.AvroToJsonConverter
import insulator.jsonhelper.jsontoavro.FieldParser
import insulator.jsonhelper.jsontoavro.JsonToAvroConverter
import insulator.jsonhelper.jsontoavro.fieldparser.ComplexTypeParsersFactory
import insulator.jsonhelper.jsontoavro.fieldparser.SimpleTypeParsersFactory
import insulator.kafka.local.LocalKafka
import insulator.kafka.local.SchemaRegistryContainer
import org.apache.avro.generic.GenericData
import org.testcontainers.containers.KafkaContainer
import javax.inject.Named
import javax.inject.Singleton

@Module
class RootModule {

    @Singleton
    @Provides
    @Named("configurationPath")
    fun provideConfigurationPath() = "${System.getProperty("user.home")}/.insulator.config"

    @Singleton
    @Provides
    fun providesConfigurationRepo(@Named("configurationPath") path: String) = ConfigurationRepo(path)

    @Singleton
    @Provides
    fun objectMapper() = ObjectMapper()

    @Singleton
    @Provides
    fun providesAvroToJsonConverter(objectMapper: ObjectMapper) = AvroToJsonConverter(objectMapper)

    @Singleton
    @Provides
    fun providesJsoToAvroConverter(objectMapper: ObjectMapper) =
        JsonToAvroConverter(objectMapper, FieldParser(SimpleTypeParsersFactory(), ComplexTypeParsersFactory()), GenericData.get())

    @Singleton
    @Provides
    fun providesJsonFormatter() = JsonFormatter()

    @Singleton
    @Provides
    fun providesLocalKafka() = KafkaContainer() // todo: fix
        .let { LocalKafka(it, SchemaRegistryContainer().withKafka("")) }
}
