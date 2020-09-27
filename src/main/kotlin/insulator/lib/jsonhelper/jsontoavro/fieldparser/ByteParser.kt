package insulator.lib.jsonhelper.jsontoavro.fieldparser

import arrow.core.Either
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
            is Float -> mapFromDecimal(fieldValue.toDouble(), schema)
            is String -> mapFromHex(fieldValue, schema)
            else -> JsonInvalidFieldException(schema, fieldValue).left()
        }
    }

    private fun mapFromHex(fieldValue: String, schema: Schema) =
        if (!fieldValue.toLowerCase().startsWith("0x")) JsonFieldParsingException("Invalid $fieldValue, BYTES value need to start with 0x").left()
        else ByteBuffer.wrap(DatatypeConverter.parseHexBinary(fieldValue.substring(2))).right()

    private fun mapFromDecimal(fieldValue: Double, schema: Schema): Either<JsonFieldParsingException, ByteBuffer> {
        if (schema.logicalType.name != "decimal") return JsonInvalidFieldException(schema, fieldValue).left()
        return Conversions.DecimalConversion()
            .runCatching { toBytes(fieldValue.toBigDecimal(), schema, schema.logicalType) }
            .toEither { JsonInvalidFieldException(schema, fieldValue) }
    }
}
