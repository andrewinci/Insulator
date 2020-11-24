package insulator.di

import dagger.BindsInstance
import dagger.Component
import insulator.CachedFactory
import insulator.kafka.model.Cluster
import insulator.views.main.consumergroup.ConsumerGroupView
import javax.inject.Inject
import javax.inject.Scope

@Scope
annotation class ConsumerGroupScope
data class ConsumerGroupId(val id: String)


class ConsumerGroupComponentFactory @Inject constructor(clusterComponent: ClusterComponent) :
    CachedFactory<ConsumerGroupId, ConsumerGroupComponent>({ consumerGroup: ConsumerGroupId ->
        DaggerConsumerGroupComponent.factory().build(clusterComponent, consumerGroup)
    })


@ConsumerGroupScope
@Component(dependencies = [ClusterComponent::class])
interface ConsumerGroupComponent {

    @Component.Factory
    interface Factory {
        fun build(component: ClusterComponent, @BindsInstance consumerGroup: ConsumerGroupId): ConsumerGroupComponent
    }

    fun cluster(): Cluster
    fun consumerGroupId(): ConsumerGroupId
    fun getConsumerGroupView(): ConsumerGroupView
}
