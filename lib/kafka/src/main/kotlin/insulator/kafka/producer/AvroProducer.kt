package insulator.kafka.producer

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import insulator.helper.runCatchingE
import insulator.kafka.SchemaRegistry
import insulator.kafka.factories.kafkaConfig
import insulator.kafka.model.Cluster
import io.confluent.kafka.serializers.KafkaAvroSerializer
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord

fun avroProducer(cluster: Cluster, schemaRegistry: SchemaRegistry, jsonToAvro: GenericJsonToAvroConverter) =
    kafkaConfig(cluster).apply {
        put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer::class.java)
    }.let {
        AvroProducer({ KafkaProducer(it) }, schemaRegistry, jsonToAvro)
    }

class AvroProducer(
    avroProducerBuilder: () -> org.apache.kafka.clients.producer.Producer<String, GenericRecord>,
    private val schemaRegistry: SchemaRegistry,
    private val jsonAvroConverter: GenericJsonToAvroConverter
) : Producer {

    private val schemaCache = HashMap<String, Either<Throwable, String>>()
    private val avroProducer: org.apache.kafka.clients.producer.Producer<String, GenericRecord>
    by lazy(avroProducerBuilder)

    override suspend fun validate(value: String, topic: String) =
        internalValidate(value, topic).flatMap { Unit.right() }

    override suspend fun send(topic: String, key: String, value: String) =
        internalValidate(value, topic)
            .map { ProducerRecord(topic, key, it) }
            .flatMap { avroProducer.runCatchingE { send(it) } }
            .map { Unit }

    override fun close() = avroProducer.close()

    private suspend fun internalValidate(value: String, topic: String) =
        getCachedSchema(topic).flatMap { jsonAvroConverter(value, it) }

    private suspend fun getCachedSchema(topic: String) =
        schemaCache.getOrPut(
            topic,
            {
                schemaRegistry.getSubject("$topic-value")
                    .map { it.schemas.maxByOrNull { s -> s.version }?.schema }
                    .flatMap { it?.right() ?: Throwable("Schema not found").left() }
            }
        )
}
