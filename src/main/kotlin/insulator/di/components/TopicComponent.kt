package insulator.di.components

import dagger.BindsInstance
import dagger.Component
import insulator.di.TopicScope
import insulator.lib.configuration.model.Cluster
import insulator.lib.kafka.model.Topic
import insulator.views.main.topic.CreateTopicView
import insulator.views.main.topic.ProducerView
import insulator.views.main.topic.TopicView

@TopicScope
@Component(dependencies = [ClusterComponent::class])
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
}
