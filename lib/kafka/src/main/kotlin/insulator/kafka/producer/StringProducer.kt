package insulator.kafka.producer

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import insulator.kafka.factories.kafkaConfig
import insulator.kafka.model.Cluster
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer

fun stringProducer(cluster: Cluster) =
    kafkaConfig(cluster).apply {
        put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
    }.let {
        StringProducer(KafkaProducer(it))
    }

class StringProducer(private val stringProducer: org.apache.kafka.clients.producer.Producer<String, String>) : Producer {
    override suspend fun validate(value: String, topic: String): Either<Throwable, Unit> = Unit.right()
    override suspend fun send(topic: String, key: String, value: String): Either<Throwable, Unit> {
        val record = ProducerRecord(topic, key, value)
        return stringProducer.runCatching { send(record) }.fold({ Unit.right() }, { it.left() })
    }

    override fun close() = stringProducer.close()
}
