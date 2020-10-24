package insulator.di.components

import dagger.BindsInstance
import dagger.Component
import insulator.di.SubjectScope
import insulator.kafka.model.Cluster
import insulator.kafka.model.Subject
import insulator.views.main.schemaregistry.SchemaView

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
