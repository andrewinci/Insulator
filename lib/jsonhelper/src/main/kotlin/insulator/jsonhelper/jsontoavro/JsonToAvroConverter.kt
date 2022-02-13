package insulator.jsonhelper.jsontoavro

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.fasterxml.jackson.databind.ObjectMapper
import insulator.helper.runCatchingE
import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericRecord

class JsonToAvroConverter(private val objectMapper: ObjectMapper, private val fieldParser: FieldParser, private val genericData: GenericData) {
    suspend fun parse(jsonString: String, schemaString: String): Either<JsonToAvroException, GenericRecord> = either {
        val jsonMap = objectMapper.runCatchingE { readValue(jsonString, Map::class.java) }.mapLeft { JsonParsingException("Invalid json", it) }.bind()
        val schema = Schema.Parser().runCatchingE { parse(schemaString) }.mapLeft { SchemaParsingException("Invalid AVRO schema", it) }.bind()
        val record = fieldParser.parseField(jsonMap, schema).flatMap {
            (it as? GenericRecord)?.right() ?: JsonToAvroException("Invalid record").left()
        }.bind()
        (
            if (genericData.validate(schema, record)) record.right()
            else JsonToAvroException("Unable to parse the json into a valid ${schema.name}. Final validation failed.").left()
            ).bind()
    }
}

interface JsonFieldParser<out O> {
    fun parse(fieldValue: Any?, schema: Schema): Either<JsonFieldParsingException, O>
}

open class JsonToAvroException(message: String? = null, cause: Throwable? = null) : Exception(message, cause)
class SchemaParsingException(message: String?, cause: Throwable? = null) : JsonToAvroException(message, cause)
class JsonParsingException(message: String?, cause: Throwable? = null) : JsonToAvroException(message, cause)

open class JsonFieldParsingException(message: String?) : JsonToAvroException(message)
class JsonInvalidFieldException(expectedSchema: Schema, fieldName: Any?) : JsonFieldParsingException("Invalid field \"$fieldName\". Expected ${expectedSchema.printType()}")
class JsonMissingFieldException(expectedSchema: Schema, val fieldName: String? = null) : JsonFieldParsingException("Missing field \"$fieldName\". Expected ${expectedSchema.printType()}")
class JsonUnexpectedFieldException(fieldName: List<Any?>) : JsonFieldParsingException("Unexpected json fields \"$fieldName\"")
