package insulator.di.components

import dagger.BindsInstance
import dagger.Component
import insulator.di.ConsumerGroupScope
import insulator.di.SubjectScope
import insulator.kafka.model.Cluster
import insulator.model.ConsumerGroupId
import insulator.views.main.consumergroup.ConsumerGroupView

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
