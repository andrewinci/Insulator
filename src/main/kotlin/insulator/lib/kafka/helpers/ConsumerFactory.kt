package insulator.lib.kafka.helpers

import insulator.di.components.ClusterComponent
import insulator.lib.kafka.DeserializationFormat
import javax.inject.Inject

class ConsumerFactory @Inject constructor(val clusterComponent: ClusterComponent) {
    fun build(valueFormat: DeserializationFormat) = when (valueFormat) {
        DeserializationFormat.Avro -> clusterComponent.avroConsumer()
        DeserializationFormat.String -> clusterComponent.genericConsumer()
    }
}
