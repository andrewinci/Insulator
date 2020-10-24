package insulator.di.components

import dagger.BindsInstance
import dagger.Component
import insulator.di.ClusterScope
import insulator.di.modules.ClusterModule
import insulator.jsonhelper.JsonFormatter
import insulator.kafka.AdminApi
import insulator.kafka.SchemaRegistry
import insulator.kafka.consumer.Consumer
import insulator.kafka.model.Cluster
import insulator.kafka.producer.AvroProducer
import insulator.kafka.producer.StringProducer
import insulator.views.configurations.ClusterView
import insulator.views.main.MainView
import insulator.views.main.schemaregistry.ListSchemaView
import insulator.views.main.topic.ListTopicView

@ClusterScope
@Component(dependencies = [InsulatorComponent::class], modules = [ClusterModule::class])
interface ClusterComponent {

    @Component.Factory
    interface Factory {
        fun build(component: InsulatorComponent, @BindsInstance cluster: Cluster): ClusterComponent
    }

    fun cluster(): Cluster

    // Views
    fun clusterView(): ClusterView
    fun mainView(): MainView
    fun listTopicView(): ListTopicView
    fun listSchemaView(): ListSchemaView

    // Admin
    fun adminApi(): AdminApi

    // Consumer
    fun consumer(): Consumer

    // Producers
    fun avroProducer(): AvroProducer
    fun stringProducer(): StringProducer

    // Schema Registry
    fun schemaRegistry(): SchemaRegistry

    // Helpers
    fun jsonFormatter(): JsonFormatter
}
