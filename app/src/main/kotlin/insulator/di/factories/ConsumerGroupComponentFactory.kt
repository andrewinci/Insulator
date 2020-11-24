package insulator.di.factories

import insulator.CachedFactory
import insulator.di.components.ClusterComponent
import insulator.di.components.ConsumerGroupComponent
import insulator.di.components.DaggerConsumerGroupComponent
import insulator.model.ConsumerGroupId
import javax.inject.Inject

class ConsumerGroupComponentFactory @Inject constructor(clusterComponent: ClusterComponent) :
    CachedFactory<ConsumerGroupId, ConsumerGroupComponent>({ consumerGroup: ConsumerGroupId ->
        DaggerConsumerGroupComponent.factory().build(clusterComponent, consumerGroup)
    })
