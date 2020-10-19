package insulator.lib.kafka

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import insulator.lib.helpers.runCatchingE
import insulator.lib.jsonhelper.jsontoavro.JsonToAvroConverter
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.producer.ProducerRecord
import java.io.Closeable
import org.apache.kafka.clients.producer.Producer as KafkaProducer

interface Producer : Closeable {
    suspend fun validate(value: String, topic: String): Either<Throwable, Unit>
    suspend fun send(topic: String, key: String, value: String): Either<Throwable, Unit>
}

class AvroProducer(
    private val avroProducer: KafkaProducer<String, GenericRecord>,
    private val schemaRegistry: SchemaRegistry,
    private val jsonAvroConverter: JsonToAvroConverter
) : Producer {

    private val schemaCache = HashMap<String, Either<Throwable, String>>()

    override suspend fun validate(value: String, topic: String) =
        internalValidate(value, topic).flatMap { Unit.right() }

    override suspend fun send(topic: String, key: String, value: String) =
        internalValidate(value, topic)
            .map { ProducerRecord(topic, key, it) }
            .flatMap { avroProducer.runCatchingE { send(it) } }
            .map { Unit }

    override fun close() = avroProducer.close()

    private suspend fun internalValidate(value: String, topic: String) =
        getCachedSchema(topic).flatMap { jsonAvroConverter.parse(jsonString = value, schemaString = it) }

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

class StringProducer(private val stringProducer: KafkaProducer<String, String>) : Producer {
    override suspend fun validate(value: String, topic: String) = Unit.right()
    override suspend fun send(topic: String, key: String, value: String): Either<Throwable, Unit> {
        val record = ProducerRecord(topic, key, value)
        return stringProducer.runCatching { send(record) }.fold({ Unit.right() }, { it.left() })
    }

    override fun close() = stringProducer.close()
}

enum class SerializationFormat {
    String,
    Avro,
}
