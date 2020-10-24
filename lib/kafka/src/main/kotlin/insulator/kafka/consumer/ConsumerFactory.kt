package insulator.kafka.consumer

import insulator.CachedFactory
import insulator.kafka.factories.kafkaConfig
import insulator.kafka.model.Cluster
import io.confluent.kafka.serializers.KafkaAvroDeserializer
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import javax.inject.Inject

class ConsumerFactory @Inject constructor(val cluster: Cluster) :
    CachedFactory<DeserializationFormat, Consumer<Any, Any>>(
        { valueFormat ->
            kafkaConfig(cluster)
                .apply {
                    put(
                        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                        when (valueFormat) {
                            DeserializationFormat.Avro -> KafkaAvroDeserializer::class.java
                            DeserializationFormat.String -> StringDeserializer::class.java
                        }
                    )
                }
                .let { KafkaConsumer(it) }
        }
    )
