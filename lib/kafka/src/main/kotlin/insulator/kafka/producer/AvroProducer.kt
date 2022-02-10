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
    producerBuilder: ProducerBuilder<GenericRecord>,
    private val schemaRegistry: SchemaRegistry,
    private val jsonAvroConverter: GenericJsonToAvroConverter
) : GenericProducer<GenericRecord>(producerBuilder) {

    private val schemaCache = HashMap<String, Either<Throwable, String>>()

    override suspend fun validate(value: String, topic: String) = internalValidate(value, topic).flatMap { Unit.right() }

    override suspend fun send(topic: String, key: String, value: String) = internalValidate(value, topic).flatMap { sendGenericRecord(topic, key, it) }

    override suspend fun sendTombstone(topic: String, key: String) = sendGenericRecord(topic, key, null)

    override fun close() = kafkaProducer.close()

    private suspend fun internalValidate(value: String, topic: String) = getCachedSchema(topic).flatMap { jsonAvroConverter(value, it) }

    private fun sendGenericRecord(topic: String, key: String, value: GenericRecord?) =
        kafkaProducer.runCatchingE { send(ProducerRecord(topic, key, value)) }.map { }

    private suspend fun getCachedSchema(topic: String) =
        schemaCache.getOrPut(
            topic
        ) {
            schemaRegistry.getSubject("$topic-value")
                .map { it.schemas.maxByOrNull { s -> s.version }?.schema }
                .flatMap { it?.right() ?: Throwable("Schema not found").left() }
        }
}
