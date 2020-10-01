package insulator.lib.jsonhelper.avrotojson

import arrow.core.left
import arrow.core.right
import org.apache.avro.Conversions
import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.apache.avro.util.Utf8
import java.nio.ByteBuffer

internal fun parseBoolean(field: Any?) = if (field is Boolean) field.right() else AvroFieldParsingException(field, "Boolean").left()

internal fun parseNumber(field: Any?) = if (field is Number) field.right() else AvroFieldParsingException(field, "Number").left()

internal fun parseEnum(field: Any?) = if (field is GenericData.EnumSymbol) field.toString().right() else AvroFieldParsingException(field, "Enum").left()

internal fun parseString(field: Any?) = if (field is String || field is Utf8) field.toString().right() else AvroFieldParsingException(field, "String").left()

internal fun parseNull(field: Any?) = if (field == null) null.right() else AvroFieldParsingException(field, "Null").left()

internal fun parseBytes(field: Any?, schema: Schema) =
    when {
        field !is ByteBuffer -> AvroFieldParsingException(field, "ByteBuffer").left()
        schema.objectProps["logicalType"] == "decimal" -> Conversions.DecimalConversion().fromBytes(field, schema, schema.logicalType).right()
        else -> ("0x" + field.array().joinToString("") { String.format("%02x", it) }).right()
    }
