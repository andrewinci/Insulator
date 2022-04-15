package insulator.jsonhelper.avrotojson

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import com.fasterxml.jackson.databind.ObjectMapper
import insulator.helper.runCatchingE
import insulator.helper.toEitherOfList
import org.apache.avro.Schema
import org.apache.avro.Schema.Type.ARRAY
import org.apache.avro.Schema.Type.BOOLEAN
import org.apache.avro.Schema.Type.BYTES
import org.apache.avro.Schema.Type.DOUBLE
import org.apache.avro.Schema.Type.ENUM
import org.apache.avro.Schema.Type.FLOAT
import org.apache.avro.Schema.Type.INT
import org.apache.avro.Schema.Type.LONG
import org.apache.avro.Schema.Type.NULL
import org.apache.avro.Schema.Type.RECORD
import org.apache.avro.Schema.Type.STRING
import org.apache.avro.Schema.Type.UNION
import org.apache.avro.generic.GenericRecord

open class AvroToJsonParsingException(message: String) : Throwable(message)
class AvroFieldParsingException(field: Any?, type: String) : AvroToJsonParsingException("Invalid field $field. Expected $type")
class UnsupportedTypeException(type: String) : AvroToJsonParsingException("Unsupported $type")

class AvroToJsonConverter(private val objectMapper: ObjectMapper) {

    fun parse(record: GenericRecord, humanReadableLogicalType: Boolean = false) =
        parseField(record, record.schema, humanReadableLogicalType)
            .flatMap { objectMapper.runCatchingE { writeValueAsString(it) } }

    private fun parseField(field: Any?, schema: Schema, humanReadableLogicalType: Boolean): Either<AvroToJsonParsingException, Any?> =
        when (schema.type) {
            RECORD -> parseRecord(field, schema, humanReadableLogicalType)
            BYTES -> parseBytes(field, schema, humanReadableLogicalType)
            UNION -> parseUnion(field, schema, humanReadableLogicalType)
            ARRAY -> parseArray(field, schema, humanReadableLogicalType)
            NULL -> parseNull(field)
            BOOLEAN -> parseBoolean(field)
            STRING -> parseString(field)
            ENUM -> parseEnum(field)
            INT, LONG, FLOAT, DOUBLE -> parseNumber(field, schema, humanReadableLogicalType)
            // missing: MAP, FIXED
            else -> UnsupportedTypeException(schema.type.getName()).left()
        }

    private fun parseRecord(field: Any?, schema: Schema, humanReadableLogicalType: Boolean): Either<AvroToJsonParsingException, Any?> {
        if (field !is GenericRecord || field.schema != schema) return AvroFieldParsingException(field, "Record").left()
        val keySchema = schema.fields.map { it.name() to it.schema() }
        return keySchema
            .map { (name, schema) -> parseField(field[name], schema, humanReadableLogicalType) }
            .toEitherOfList()
            .map { values -> keySchema.map { it.first }.zip(values).toMap() }
    }

    private fun parseUnion(field: Any?, schema: Schema, humanReadableLogicalType: Boolean) =
        schema.types.map { t -> parseField(field, t, humanReadableLogicalType) }
            .let { attempts -> attempts.firstOrNull { it.isRight() } ?: attempts.first() }

    private fun parseArray(field: Any?, schema: Schema, humanReadableLogicalType: Boolean) =
        if (field is Collection<*>) field.map { parseField(it, schema.elementType, humanReadableLogicalType) }.toEitherOfList()
        else AvroFieldParsingException(field, "Array").left()
}
