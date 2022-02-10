package insulator.kafka.producer

import arrow.core.*
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

    override suspend fun validate(value: String, topic: String, schemaVersion: Int?) =
        internalValidate(value, topic, schemaVersion).flatMap { Unit.right() }

    override suspend fun send(topic: String, key: String, value: String, schemaVersion: Int?) =
        internalValidate(value, topic, schemaVersion).flatMap { sendGenericRecord(topic, key, it) }

    override suspend fun sendTombstone(topic: String, key: String) = sendGenericRecord(topic, key, null)

    override fun close() = kafkaProducer.close()

    private suspend fun internalValidate(value: String, topic: String, version: Int?) = schemaRegistry
        .getSubject("$topic-value")
        .map { s -> s.schemas }
        .map { schemas -> if (version != null)
            schemas.firstOrNull { it.version == version }
            else schemas.maxByOrNull { it.version }
        }
        .flatMap { schemaVersion ->
            if (schemaVersion == null)  Left(Throwable("Schema not found: $topic - v$version"))
            else Right(schemaVersion.schema)
        }
        .flatMap { schema -> jsonAvroConverter(value, schema) }

    private fun sendGenericRecord(topic: String, key: String, value: GenericRecord?) =
        kafkaProducer.runCatchingE { send(ProducerRecord(topic, key, value)) }.map { }

}
