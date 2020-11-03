package insulator.di.modules

import dagger.Module
import dagger.Provides
import insulator.di.TopicScope
import insulator.jsonhelper.avrotojson.AvroToJsonConverter
import insulator.kafka.consumer.consumer
import insulator.kafka.model.Cluster

@Module
class TopicModule {

    @Provides
    @TopicScope
    fun providesConsumer(cluster: Cluster, converter: AvroToJsonConverter) =
        consumer(cluster, converter::parse)
}
