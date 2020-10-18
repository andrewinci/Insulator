package insulator.di.dagger.components

import dagger.BindsInstance
import dagger.Component
import insulator.di.dagger.SubjectScope
import insulator.di.dagger.TopicScope
import insulator.lib.configuration.model.Cluster
import insulator.lib.kafka.model.Subject
import insulator.lib.kafka.model.Topic
import insulator.views.main.schemaregistry.SchemaView
import insulator.views.main.topic.CreateTopicView
import insulator.views.main.topic.TopicView

@SubjectScope
@Component(dependencies = [ClusterComponent::class])
interface SubjectComponent {

    @Component.Factory
    interface Factory {
        fun build(component: ClusterComponent, @BindsInstance subject: Subject): SubjectComponent
    }

    fun cluster(): Cluster
    fun subject(): Subject
    fun getSchemaView(): SchemaView
}