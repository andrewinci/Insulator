package insulator.di.modules

import dagger.Module
import dagger.Provides
import insulator.di.ClusterScope
import insulator.di.components.ClusterComponent
import insulator.di.components.DaggerSubjectComponent
import insulator.di.components.DaggerTopicComponent
import insulator.di.components.SubjectComponent
import insulator.di.components.TopicComponent
import insulator.di.factories.Factory
import insulator.di.factories.cachedFactory
import insulator.lib.jsonhelper.avrotojson.AvroToJsonConverter
import insulator.lib.jsonhelper.jsontoavro.JsonToAvroConverter
import insulator.lib.kafka.AdminApi
import insulator.lib.kafka.AvroProducer
import insulator.lib.kafka.SchemaRegistry
import insulator.lib.kafka.StringProducer
import insulator.lib.kafka.helpers.ConsumerFactory
import insulator.lib.kafka.model.Subject
import insulator.lib.kafka.model.Topic
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.producer.Producer
import insulator.lib.kafka.Consumer as InsulatorConsumer

@Module
class ClusterModule {

    @Provides
    @ClusterScope
    fun providesAdminApi(admin: AdminClient, consumer: Consumer<Any, Any>) = AdminApi(admin, consumer)

    @Provides
    @ClusterScope
    fun providesConsumer(converter: AvroToJsonConverter, consumerFactory: ConsumerFactory) =
        InsulatorConsumer(converter, consumerFactory)

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
