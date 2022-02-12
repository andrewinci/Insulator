package insulator.jsonhelper.jsontoavro.fieldparser

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import insulator.helper.runCatchingE
import insulator.jsonhelper.jsontoavro.JsonFieldParser
import insulator.jsonhelper.jsontoavro.JsonFieldParsingException
import insulator.jsonhelper.jsontoavro.JsonInvalidFieldException
import org.apache.avro.Conversions
import org.apache.avro.Schema
import java.nio.ByteBuffer

class ByteParser : JsonFieldParser<ByteBuffer> {
    override fun parse(fieldValue: Any?, schema: Schema): Either<JsonFieldParsingException, ByteBuffer> {
        if (fieldValue == null) return JsonInvalidFieldException(schema, fieldValue).left()
        return when (fieldValue) {
            is Double -> mapFromDecimal(fieldValue, schema)
            is String -> mapFromHex(fieldValue)
            else -> JsonInvalidFieldException(schema, fieldValue).left()
        }
    }

    private fun String.toBinaryArray() = this.chunked(2).map { it.uppercase().toInt(16).toByte() }.toByteArray()

    private fun mapFromHex(fieldValue: String) =
        if (!fieldValue.lowercase().startsWith("0x")) JsonFieldParsingException("Invalid $fieldValue, BYTES value need to start with 0x").left()
        else ByteBuffer.wrap(fieldValue.substring(2).toBinaryArray()).right()

    private fun mapFromDecimal(fieldValue: Double, schema: Schema): Either<JsonFieldParsingException, ByteBuffer> {
        if (schema.objectProps["logicalType"] != "decimal") return JsonFieldParsingException("Invalid $fieldValue, decimal value expected").left()
        val scale = schema.objectProps["scale"] as? Int ?: 0
        return fieldValue.toBigDecimal().runCatchingE { setScale(scale) }
            .mapLeft { JsonFieldParsingException("Invalid decimal $fieldValue. Max scale must be $scale") }
            .flatMap {
                Conversions.DecimalConversion()
                    .runCatchingE { toBytes(it, schema, schema.logicalType) }
                    .mapLeft { JsonInvalidFieldException(schema, fieldValue) }
            }
    }
}
