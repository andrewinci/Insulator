package insulator.lib.kafka.helpers

import insulator.di.components.ClusterComponent
import insulator.di.factories.CachedFactory
import insulator.lib.kafka.DeserializationFormat
import org.apache.kafka.clients.consumer.Consumer
import javax.inject.Inject

class ConsumerFactory @Inject constructor(val clusterComponent: ClusterComponent) :
    CachedFactory<DeserializationFormat, Consumer<Any, Any>>(
        { valueFormat ->
            when (valueFormat) {
                DeserializationFormat.Avro -> clusterComponent.avroConsumer()
                DeserializationFormat.String -> clusterComponent.genericConsumer()
            }
        }
    )
