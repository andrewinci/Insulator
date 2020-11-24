package insulator.di

import dagger.BindsInstance
import dagger.Component
import insulator.CachedFactory
import insulator.kafka.model.Cluster
import insulator.kafka.model.Subject
import insulator.views.main.schemaregistry.CreateSchemaView
import insulator.views.main.schemaregistry.SchemaView
import javax.inject.Inject
import javax.inject.Scope

@Scope
annotation class SubjectScope

class SubjectComponentFactory @Inject constructor(clusterComponent: ClusterComponent) :
    CachedFactory<Subject, SubjectComponent>({ subject: Subject ->
        DaggerSubjectComponent.factory().build(clusterComponent, subject)
    })


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

    fun getCreateSchemaView(): CreateSchemaView
}
