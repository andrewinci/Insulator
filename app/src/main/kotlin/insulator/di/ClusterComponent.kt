package insulator.di

import dagger.BindsInstance
import dagger.Component
import insulator.CachedFactory
import insulator.di.modules.ClusterModule
import insulator.jsonhelper.JsonFormatter
import insulator.jsonhelper.avrotojson.AvroToJsonConverter
import insulator.kafka.AdminApi
import insulator.kafka.SchemaRegistry
import insulator.kafka.model.Cluster
import insulator.kafka.producer.AvroProducer
import insulator.kafka.producer.StringProducer
import insulator.ui.WindowsManager
import insulator.views.configurations.ClusterView
import insulator.views.main.MainView
import insulator.views.main.consumergroup.ListConsumerGroupView
import insulator.views.main.schemaregistry.ListSchemaView
import insulator.views.main.topic.ListTopicView
import javax.inject.Inject
import javax.inject.Scope

@Scope
annotation class ClusterScope

class ClusterComponentFactory @Inject constructor(insulatorComponent: InsulatorComponent) :
    CachedFactory<Cluster, ClusterComponent>({ cluster ->
        DaggerClusterComponent.factory().build(insulatorComponent, cluster)
    })

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
    fun listConsumerGroupView(): ListConsumerGroupView

    // Admin
    fun adminApi(): AdminApi

    // Producers
    fun avroProducer(): AvroProducer?
    fun stringProducer(): StringProducer

    // Schema Registry
    fun schemaRegistry(): SchemaRegistry?

    // Helpers
    fun jsonFormatter(): JsonFormatter
    fun avroToJsonConverter(): AvroToJsonConverter

    // Windows manager
    fun windowsManager(): WindowsManager
}
