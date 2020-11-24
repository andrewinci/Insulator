package insulator.di

import dagger.BindsInstance
import dagger.Component
import insulator.CachedFactory
import insulator.di.modules.TopicModule
import insulator.jsonhelper.JsonFormatter
import insulator.kafka.model.Cluster
import insulator.kafka.model.Topic
import insulator.views.main.topic.CreateTopicView
import insulator.views.main.topic.ProducerView
import insulator.views.main.topic.TopicView
import javax.inject.Inject
import javax.inject.Scope

@Scope
annotation class TopicScope

class TopicComponentFactory @Inject constructor(clusterComponent: ClusterComponent) :
    CachedFactory<Topic, TopicComponent>({ topic: Topic ->
        DaggerTopicComponent.factory().build(clusterComponent, topic)
    })

@TopicScope
@Component(dependencies = [ClusterComponent::class], modules = [TopicModule::class])
interface TopicComponent {

    @Component.Factory
    interface Factory {
        fun build(component: ClusterComponent, @BindsInstance topic: Topic): TopicComponent
    }

    fun cluster(): Cluster
    fun topic(): Topic
    fun getCreateTopicView(): CreateTopicView
    fun getTopicView(): TopicView
    fun getProducerView(): ProducerView
    fun jsonFormatter(): JsonFormatter
}
