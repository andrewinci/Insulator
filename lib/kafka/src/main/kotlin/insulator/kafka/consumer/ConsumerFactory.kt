package insulator.kafka.consumer

import insulator.CachedFactory
import insulator.kafka.factories.kafkaConfig
import insulator.kafka.model.Cluster
import insulator.kafka.producer.SerializationFormat
import io.confluent.kafka.serializers.KafkaAvroDeserializer
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer

class ConsumerFactory(val cluster: Cluster) :
    CachedFactory<SerializationFormat, Consumer<Any, Any>>(
        { valueFormat ->
            kafkaConfig(cluster)
                .apply {
                    put(
                        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                        when (valueFormat) {
                            SerializationFormat.Avro -> KafkaAvroDeserializer::class.java
                            SerializationFormat.String -> StringDeserializer::class.java
                        }
                    )
                }
                .let { KafkaConsumer(it) }
        }
    )
