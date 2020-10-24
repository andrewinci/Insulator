package insulator.di.components

import dagger.BindsInstance
import dagger.Component
import insulator.di.ClusterScope
import insulator.di.modules.ClusterModule
import insulator.di.modules.KafkaModule
import insulator.lib.configuration.model.Cluster
import insulator.lib.jsonhelper.JsonFormatter
import insulator.lib.kafka.AdminApi
import insulator.lib.kafka.AvroProducer
import insulator.lib.kafka.SchemaRegistry
import insulator.lib.kafka.StringProducer
import insulator.views.configurations.ClusterView
import insulator.views.main.MainView
import insulator.views.main.schemaregistry.ListSchemaView
import insulator.views.main.topic.ListTopicView
import org.apache.kafka.clients.consumer.Consumer
import javax.inject.Named

@ClusterScope
@Component(dependencies = [InsulatorComponent::class], modules = [KafkaModule::class, ClusterModule::class])
interface ClusterComponent {

    @Component.Factory
    interface Factory {
        fun build(component: InsulatorComponent, @BindsInstance cluster: Cluster): ClusterComponent
    }

    fun cluster(): Cluster

    // Views
    fun clusterView(): ClusterView
    fun mainView(): MainView

    // Admin
    fun adminApi(): AdminApi

    // Consumers
    fun stringConsumer(): Consumer<Any, Any>
    @Named("avroConsumer")
    fun avroConsumer(): Consumer<Any, Any>
    fun consumer(): insulator.lib.kafka.Consumer

    // Producers
    fun avroProducer(): AvroProducer
    fun stringProducer(): StringProducer

    // Schema Registry
    fun schemaRegistry(): SchemaRegistry

    // Helpers
    fun jsonFormatter(): JsonFormatter

    // Views
    fun getListTopicView(): ListTopicView
    fun getListSchemaView(): ListSchemaView
}
