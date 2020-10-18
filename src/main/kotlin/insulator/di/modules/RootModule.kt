package insulator.di.modules

import com.fasterxml.jackson.databind.ObjectMapper
import dagger.Module
import dagger.Provides
import insulator.di.components.ClusterComponent
import insulator.di.components.DaggerClusterComponent
import insulator.di.components.InsulatorComponent
import insulator.di.factories.Factory
import insulator.di.factories.cachedFactory
import insulator.lib.configuration.ConfigurationRepo
import insulator.lib.configuration.model.Cluster
import insulator.lib.jsonhelper.JsonFormatter
import insulator.lib.jsonhelper.avrotojson.AvroToJsonConverter
import insulator.lib.jsonhelper.jsontoavro.FieldParser
import insulator.lib.jsonhelper.jsontoavro.JsonToAvroConverter
import insulator.lib.jsonhelper.jsontoavro.fieldparser.ComplexTypeParsersFactory
import insulator.lib.jsonhelper.jsontoavro.fieldparser.SimpleTypeParsersFactory
import kotlinx.serialization.json.Json
import org.apache.avro.generic.GenericData
import javax.inject.Named
import javax.inject.Singleton

@Module
class RootModule {

    @Singleton
    @Provides
    fun provideJson(): Json = Json { }

    @Singleton
    @Provides
    @Named("configurationPath")
    fun provideConfigurationPath() = "${System.getProperty("user.home")}/.insulator.config"

    @Singleton
    @Provides
    fun providesClusterComponentFactory(insulatorComponent: InsulatorComponent): Factory<Cluster, ClusterComponent> =
        cachedFactory { cluster: Cluster -> DaggerClusterComponent.factory().build(insulatorComponent, cluster) }

    @Singleton
    @Provides
    fun providesConfigurationRepo(json: Json, @Named("configurationPath") path: String) = ConfigurationRepo(json, path)

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
    fun providesJsonFormatter(json: Json) = JsonFormatter(json)

}
