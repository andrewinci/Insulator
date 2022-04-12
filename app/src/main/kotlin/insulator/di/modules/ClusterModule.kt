package insulator.di.modules

import dagger.Module
import dagger.Provides
import insulator.di.ClusterScope
import insulator.helper.GlobalState
import insulator.jsonhelper.avrotojson.AvroToJsonConverter
import insulator.jsonhelper.jsontoavro.JsonToAvroConverter
import insulator.kafka.SchemaRegistry
import insulator.kafka.adminApi
import insulator.kafka.consumer.consumer
import insulator.kafka.model.Cluster
import insulator.kafka.producer.avroProducer
import insulator.kafka.producer.stringProducer
import insulator.kafka.schemaRegistry

@Module
class ClusterModule {

    @Provides
    @ClusterScope
    fun providesAdminApi(cluster: Cluster) = adminApi(cluster)

    @Provides
    @ClusterScope
    fun providesConsumer(cluster: Cluster, converter: AvroToJsonConverter) =
        consumer(cluster) { converter.parse(it, GlobalState.humanReadableAvroProperty.value) }

    @Provides
    @ClusterScope
    fun providesAvroProducer(cluster: Cluster, schemaRegistry: SchemaRegistry?, jsonAvroConverter: JsonToAvroConverter) =
        if (schemaRegistry != null) avroProducer(cluster, schemaRegistry, jsonAvroConverter::parse) else null

    @Provides
    @ClusterScope
    fun providesStringProducer(cluster: Cluster) = stringProducer(cluster)

    @Provides
    @ClusterScope
    fun providesSchemaRegistry(cluster: Cluster) =
        if (cluster.isSchemaRegistryConfigured()) schemaRegistry(cluster) else null
}
