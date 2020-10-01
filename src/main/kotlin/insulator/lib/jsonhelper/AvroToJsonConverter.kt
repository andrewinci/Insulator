package insulator.lib.jsonhelper

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.fasterxml.jackson.databind.ObjectMapper
import insulator.lib.helpers.toEither
import insulator.lib.helpers.toEitherOfList
import org.apache.avro.Conversions
import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericRecord
import org.apache.avro.util.Utf8
import java.nio.ByteBuffer

open class AvroToJsonParsingException(message: String) : Throwable(message)
class AvroFieldParsingException(field: Any?, type: String) : AvroToJsonParsingException("Invalid field $field. Expected $type")

class AvroToJsonConverter(private val objectMapper: ObjectMapper) {

    fun parse(record: GenericRecord) =
        parseField(record, record.schema)
            .flatMap {
                objectMapper.runCatching { writeValueAsString(it) }.toEither { AvroToJsonParsingException("Unable to write the json") }
            }

    private fun parseField(field: Any?, schema: Schema): Either<AvroFieldParsingException, Any?> {
        return when (schema.type) {
            Schema.Type.RECORD -> parseRecord(field, schema)
            Schema.Type.BYTES -> parseBytes(field, schema)
            Schema.Type.UNION -> parseUnion(field, schema)
            Schema.Type.ARRAY -> parseArray(field, schema)
            Schema.Type.NULL -> parseNull(field)
            Schema.Type.INT, Schema.Type.LONG, Schema.Type.FLOAT, Schema.Type.DOUBLE -> parseNumber(field)
            Schema.Type.BOOLEAN -> parseBoolean(field)
            Schema.Type.STRING -> parseString(field)
            Schema.Type.ENUM -> parseEnum(field)
// todo            Schema.Type.MAP -> checkType<String>()
// todo            Schema.Type.FIXED -> checkType<String>()

            else -> field.toString().right()
        }
    }

    private fun parseBoolean(field: Any?) =
        if (field is Boolean) field.right() else AvroFieldParsingException(field, "Boolean").left()

    private fun parseNull(field: Any?) =
        if (field == null) null.right() else AvroFieldParsingException(field, "Null").left()

    private fun parseNumber(field: Any?) =
        if (field is Number) field.right()
        else AvroFieldParsingException(field, "Number").left()

    private fun parseEnum(field: Any?) =
        (field as? GenericData.EnumSymbol)?.toString()?.right()
            ?: AvroFieldParsingException(field, "Enum").left()

    private fun parseString(field: Any?) =
        when (field) {
            is String, is Utf8 -> field.toString().right()
            else -> AvroFieldParsingException(field, "String").left()
        }

    private fun parseRecord(field: Any?, schema: Schema): Either<AvroFieldParsingException, Any?> {
        if (field !is GenericRecord) return AvroFieldParsingException(field, "Record").left()
        val keySchema = schema.fields.map { it.name() to it.schema() }
        return keySchema
            .map { (name, schema) -> parseField(field[name], schema) }
            .toEitherOfList()
            .map { values -> keySchema.map { it.first }.zip(values).toMap() }
    }

    private fun parseArray(field: Any?, schema: Schema): Either<AvroFieldParsingException, Any?> {
        if (field !is GenericData.Array<*>) return AvroFieldParsingException(field, "Array").left()
        return field.map { parseField(it, schema.elementType) }.toEitherOfList()
    }

    private fun parseUnion(field: Any?, schema: Schema): Either<AvroFieldParsingException, Any?> {
        val mapAttempts = schema.types.map { t -> parseField(field, t) }
        return mapAttempts.firstOrNull { it.isRight() } ?: mapAttempts.first()
    }

    private fun parseBytes(field: Any?, schema: Schema): Either<AvroFieldParsingException, Any?> {
        if (field !is ByteBuffer) return AvroFieldParsingException(field, "ByteBuffer").left()
        return if (schema.objectProps["logicalType"] == "decimal") Conversions.DecimalConversion().fromBytes(field, schema, schema.logicalType).right()
        else ("0x" + field.array().joinToString("") { String.format("%02x", it) }).right()
    }
}
