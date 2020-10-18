package insulator.di.dagger.modules

import dagger.Module
import dagger.Provides
import insulator.di.dagger.ClusterScope
import insulator.di.dagger.components.ClusterComponent
import insulator.di.dagger.components.DaggerSubjectComponent
import insulator.di.dagger.components.DaggerTopicComponent
import insulator.di.dagger.components.SubjectComponent
import insulator.di.dagger.components.TopicComponent
import insulator.di.dagger.factories.Factory
import insulator.di.dagger.factories.cachedFactory
import insulator.lib.jsonhelper.avrotojson.AvroToJsonConverter
import insulator.lib.jsonhelper.jsontoavro.JsonToAvroConverter
import insulator.lib.kafka.AdminApi
import insulator.lib.kafka.AvroProducer
import insulator.lib.kafka.SchemaRegistry
import insulator.lib.kafka.StringProducer
import insulator.lib.kafka.model.Subject
import insulator.lib.kafka.model.Topic
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.producer.Producer
import javax.inject.Named
import insulator.lib.kafka.Consumer as InsulatorConsumer

@Module
class ClusterModule {

    @Provides
    @ClusterScope
    fun providesAdminApi(admin: AdminClient, consumer: Consumer<Any, Any>) = AdminApi(admin, consumer)

    @Provides
    @ClusterScope
    fun providesConsumer(converter: AvroToJsonConverter, stringConsumer: Consumer<Any, Any>, @Named("avroConsumer") avroConsumer: Consumer<Any, Any>) =
        InsulatorConsumer(converter, stringConsumer, avroConsumer)

    @Provides
    fun providesAvroProducer(avroProducer: Producer<String, GenericRecord>, schemaRegistry: SchemaRegistry, jsonAvroConverter: JsonToAvroConverter) =
        AvroProducer(avroProducer, schemaRegistry, jsonAvroConverter)

    @Provides
    fun providesStringProducer(stringProducer: Producer<String, String>) = StringProducer(stringProducer)

    @Provides
    fun schemaRegistry(client: SchemaRegistryClient) = SchemaRegistry(client)

    // Factories
    @Provides
    @ClusterScope
    fun providesTopicFactory(clusterComponent: ClusterComponent): Factory<Topic, TopicComponent> =
        cachedFactory { topic: Topic -> DaggerTopicComponent.factory().build(clusterComponent, topic) }

    @Provides
    @ClusterScope
    fun providesSubjectFactory(clusterComponent: ClusterComponent): Factory<Subject, SubjectComponent> =
        cachedFactory { subject: Subject -> DaggerSubjectComponent.factory().build(clusterComponent, subject) }
}
