package insulator.jsonhelper.avrotojson

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.apache.avro.Conversions
import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.apache.avro.util.Utf8
import java.nio.ByteBuffer
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

internal fun parseBoolean(field: Any?) =
    if (field is Boolean) field.right() else AvroFieldParsingException(field, "Boolean").left()

internal fun parseNumber(field: Any?, schema: Schema, humanReadableLogicalType: Boolean): Either<AvroToJsonParsingException, Any?> {
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.of("UTC"))
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS").withZone(ZoneId.of("UTC"))
    return when {
        humanReadableLogicalType && schema.objectProps["logicalType"] == "date" && field is Int -> {
            val instant = Instant.EPOCH.plus(field.toLong(), ChronoUnit.DAYS)
            dateFormatter.format(instant).right()
        }
        humanReadableLogicalType && schema.objectProps["logicalType"] == "timestamp-millis" && field is Long -> {
            Instant.EPOCH.plus(field, ChronoUnit.MILLIS).toString().right()
        }
        humanReadableLogicalType && schema.objectProps["logicalType"] == "timestamp-micros" && field is Long -> {
            Instant.EPOCH.plus(field, ChronoUnit.MICROS).toString().right()
        }
        humanReadableLogicalType && schema.objectProps["logicalType"] == "time-millis" && field is Int -> {
            val instant = Instant.EPOCH.plus(field.toLong(), ChronoUnit.MILLIS)
            timeFormatter.format(instant).right()
        }
        humanReadableLogicalType && schema.objectProps["logicalType"] == "time-micros" && field is Long -> {
            val instant = Instant.EPOCH.plus(field.toLong(), ChronoUnit.MICROS)
            timeFormatter.format(instant).right()
        }
        field is Number -> field.right()
        else -> AvroFieldParsingException(field, "Number").left()
    }
}

internal fun parseEnum(field: Any?) =
    if (field is GenericData.EnumSymbol) field.toString().right() else AvroFieldParsingException(field, "Enum").left()

internal fun parseString(field: Any?) =
    if (field is String || field is Utf8) field.toString().right() else AvroFieldParsingException(
        field,
        "String"
    ).left()

internal fun parseNull(field: Any?) =
    if (field == null) null.right() else AvroFieldParsingException(field, "Null").left()

internal fun parseBytes(field: Any?, schema: Schema, humanReadableLogicalType: Boolean = false) =
    when {
        field !is ByteBuffer -> AvroFieldParsingException(field, "ByteBuffer").left()
        humanReadableLogicalType && schema.objectProps["logicalType"] == "decimal" -> Conversions.DecimalConversion()
            .fromBytes(field, schema, schema.logicalType).right()
        else -> ("0x" + field.array().joinToString("") { String.format("%02x", it) }).right()
    }
