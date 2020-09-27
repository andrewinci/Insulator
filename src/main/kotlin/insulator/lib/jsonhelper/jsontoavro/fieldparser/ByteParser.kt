package insulator.lib.jsonhelper.jsontoavro.fieldparser

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import insulator.lib.helpers.toEither
import insulator.lib.jsonhelper.jsontoavro.JsonFieldParser
import insulator.lib.jsonhelper.jsontoavro.JsonFieldParsingException
import insulator.lib.jsonhelper.jsontoavro.JsonInvalidFieldException
import org.apache.avro.Conversions
import org.apache.avro.Schema
import java.nio.ByteBuffer
import javax.xml.bind.DatatypeConverter

class ByteParser : JsonFieldParser<ByteBuffer> {
    override fun parse(fieldValue: Any?, schema: Schema): Either<JsonFieldParsingException, ByteBuffer> {
        if (fieldValue == null) return JsonInvalidFieldException(schema, fieldValue).left()
        return when (fieldValue) {
            is Double -> mapFromDecimal(fieldValue, schema)
            is String -> mapFromHex(fieldValue, schema)
            else -> JsonInvalidFieldException(schema, fieldValue).left()
        }
    }

    private fun mapFromHex(fieldValue: String, schema: Schema) =
        if (!fieldValue.toLowerCase().startsWith("0x")) JsonFieldParsingException("Invalid $fieldValue, BYTES value need to start with 0x").left()
        else ByteBuffer.wrap(DatatypeConverter.parseHexBinary(fieldValue.substring(2))).right()

    private fun mapFromDecimal(fieldValue: Double, schema: Schema): Either<JsonFieldParsingException, ByteBuffer> {
        if (schema.objectProps["logicalType"] != "decimal") return JsonFieldParsingException("Invalid $fieldValue, decimal value expected").left()
        val scale = schema.objectProps["scale"] as? Int ?: 0
        return fieldValue.toBigDecimal().runCatching { setScale(scale) }
            .toEither { JsonFieldParsingException("Invalid decimal $fieldValue. Max scale must be $scale") }
            .flatMap {
                Conversions.DecimalConversion()
                    .runCatching { toBytes(it, schema, schema.logicalType) }
                    .toEither { JsonInvalidFieldException(schema, fieldValue) }
            }
    }
}
