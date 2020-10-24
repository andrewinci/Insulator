package insulator.kafka.producer

import arrow.core.Either
import org.apache.avro.generic.GenericRecord
import java.io.Closeable

interface Producer : Closeable {
    suspend fun validate(value: String, topic: String): Either<Throwable, Unit>
    suspend fun send(topic: String, key: String, value: String): Either<Throwable, Unit>
}

typealias GenericJsonToAvroConverter = suspend ((jsonString: String, schemaString: String) -> Either<Throwable, GenericRecord>)

enum class SerializationFormat {
    String,
    Avro,
}
