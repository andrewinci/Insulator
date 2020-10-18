package insulator.di.factories

import insulator.di.components.ClusterComponent
import insulator.di.components.DaggerTopicComponent
import insulator.di.components.TopicComponent
import insulator.lib.kafka.model.Topic
import javax.inject.Inject

class TopicComponentFactory @Inject constructor(clusterComponent: ClusterComponent) :
    CachedFactory<Topic, TopicComponent>({ topic: Topic ->
        DaggerTopicComponent.factory().build(clusterComponent, topic)
    })
