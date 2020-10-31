package insulator.kafka.producer

import arrow.core.Either
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.producer.Producer as KafkaProducer
import java.io.Closeable

typealias ProducerBuilder<V> = () -> KafkaProducer<String, V>
typealias GenericJsonToAvroConverter = suspend ((jsonString: String, schemaString: String) -> Either<Throwable, GenericRecord>)

interface Producer : Closeable {
    suspend fun validate(value: String, topic: String): Either<Throwable, Unit>
    suspend fun send(topic: String, key: String, value: String): Either<Throwable, Unit>
}

abstract class GenericProducer<V>(producerBuilder: ProducerBuilder<V>) : Producer {
    protected val kafkaProducer: KafkaProducer<String, V> by lazy(producerBuilder)
}

enum class SerializationFormat {
    String,
    Avro,
}
