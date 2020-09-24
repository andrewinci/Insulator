package insulator.lib.kafka

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import insulator.lib.configuration.model.Cluster
import insulator.lib.jsonhelper.JsonToAvroConverter
import org.apache.kafka.clients.producer.ProducerRecord
import org.koin.core.KoinComponent
import org.koin.core.qualifier.named
import org.koin.ext.scope
import org.apache.kafka.clients.producer.Producer as KafkaProducer

class Producer(val cluster: Cluster, private val jsonAvroConverter: JsonToAvroConverter) : KoinComponent {
    private val schemaCache = HashMap<String, Either<Throwable, String>>()
    private val schemaRegistry = cluster.scope.get<SchemaRegistry>()
    private val avroProducer = cluster.scope.get<KafkaProducer<Any, Any>>(named("avroProducer"))

    fun validate(value: String, topic: String) =
        getCachedSchema(topic).flatMap { jsonAvroConverter.convert(value, it) }

    fun produce(topic: String, key: String, value: String) =
        validate(value, topic)
            .map { ProducerRecord<Any, Any>(topic, key, it) }
            .flatMap { avroProducer.runCatching { send(it) }.fold({ Unit.right() }, { it.left() }) }

    private fun getCachedSchema(topic: String) =
        schemaCache.getOrPut(topic,
            {
                schemaRegistry.getSubject("$topic-value")
                    .map { it.schemas.maxByOrNull { s -> s.version }?.schema }
                    .flatMap { it?.right() ?: Throwable("Schema not found").left() }
            })
}
