package insulator.lib.jsonhelper

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.avro.Conversions
import org.apache.avro.Schema
import org.apache.avro.Schema.Parser
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericRecord
import org.apache.avro.generic.GenericRecordBuilder
import java.nio.ByteBuffer
import javax.xml.bind.DatatypeConverter

class JsonToAvroConverter(private val objectMapper: ObjectMapper, private val schemaParser: Parser) {

    fun convert(jsonString: String, schemaString: String): Either<Throwable, GenericRecord> {
        return try {
            val jsonMap = objectMapper.readValue(jsonString, Map::class.java)
            val schema = schemaParser.parse(schemaString)
            val parsed = parseRecord(schema, jsonMap)
            if (GenericData().validate(schema, parsed)) {
                parsed.right()
            } else JsonToAvroException("Generated record is invalid, check the schema").left()
        } catch (jsonException: JsonProcessingException) {
            InvalidJsonException().left()
        }
    }

    private fun parseRecord(schema: Schema, jsonMap: Map<*, *>?): GenericRecord {
        if (schema.type != Schema.Type.RECORD || jsonMap == null)
            throw JsonToAvroException("Expecting record ${schema.name}")
        val recordBuilder = GenericRecordBuilder(schema)
        schema.fields.forEach { fieldSchema ->
            val fieldName = fieldSchema.name()
            if (fieldName !in jsonMap) throw JsonToAvroException("Expecting \"${fieldName}\" with type ${printType(fieldSchema)}", fieldName)
            val jsonValue = jsonMap[fieldName]

            recordBuilder.set(fieldSchema, parseField(fieldSchema.schema(), jsonValue))
        }
        return recordBuilder.build()
    }

    private fun printType(fieldSchema: Schema.Field): String {
        val schema = fieldSchema.schema()
        return when (schema.type) {
            Schema.Type.NULL -> "null"
            Schema.Type.RECORD -> "record: ${schema.name}"
            Schema.Type.BYTES -> "bytes (eg \"0x00\")"
            Schema.Type.ENUM -> "enum [${fieldSchema.schema().enumSymbols.joinToString(", ")}]"
            Schema.Type.UNION -> "union [${fieldSchema.schema().types.joinToString(", ") { it.name }}]"
            Schema.Type.ARRAY -> "array of ${fieldSchema.schema().elementType.name}"
            else -> schema.type.name.toLowerCase()
        }
    }

    private fun parseField(fieldSchema: Schema, jsonValue: Any?): Any? {
        return when (fieldSchema.type) {
            Schema.Type.NULL -> if (jsonValue == null) null else throw JsonToAvroException("$jsonValue should be NULL")
            Schema.Type.RECORD -> parseRecord(fieldSchema, jsonValue as? Map<*, *>)
            Schema.Type.FLOAT -> parseFloat(fieldSchema, jsonValue)
            Schema.Type.BYTES -> parseBytes(fieldSchema, jsonValue)
            Schema.Type.ENUM -> parseEnum(fieldSchema, jsonValue)
            Schema.Type.UNION -> parseUnion(fieldSchema, jsonValue)
            Schema.Type.ARRAY -> parseArray(fieldSchema, jsonValue)
            Schema.Type.LONG -> parseLong(jsonValue)
            else -> jsonValue
        }
    }

    private fun parseLong(jsonValue: Any?) =
        when (jsonValue) {
            is Long -> jsonValue
            is Int -> jsonValue.toLong()
            else -> throw JsonToAvroException("Expecting long but got ${jsonValue?.javaClass?.simpleName}")
        }

    private fun parseArray(fieldSchema: Schema, jsonValue: Any?): Any {
        if (jsonValue !is ArrayList<*>) throw JsonToAvroException("Expecting array of \"${fieldSchema.elementType.name}\" but got $jsonValue")
        return jsonValue.map { parseField(fieldSchema.elementType, it) }.toList()
    }

    private fun parseUnion(fieldSchema: Schema, jsonValue: Any?): Any? {
        fieldSchema.types.forEach {
            val parsed = kotlin.runCatching { parseField(it, jsonValue) }
            if (parsed.isSuccess) return parsed.getOrNull()
        }
        throw JsonToAvroException("Expecting \"${fieldSchema.fields.first().name()}\" with type Union [${fieldSchema.types.joinToString(", ") { it.name }}]")
    }

    private fun parseEnum(fieldSchema: Schema, jsonValue: Any?): GenericData.EnumSymbol {
        val symbols = fieldSchema.enumSymbols
        return if (jsonValue == null || jsonValue.toString() !in symbols)
            throw JsonToAvroException("Expecting enum [${symbols.joinToString(", ")}]")
        else GenericData.EnumSymbol(fieldSchema, jsonValue)
    }

    private fun parseBytes(fieldSchema: Schema, jsonValue: Any?): ByteBuffer? {
        if (jsonValue == null) throw JsonToAvroException("Expecting bytes but got \"${jsonValue}\"")
        if (jsonValue is Double && fieldSchema.logicalType.name == "decimal")
            return Conversions.DecimalConversion().toBytes(jsonValue.toBigDecimal(), fieldSchema, fieldSchema.logicalType)
        return when (jsonValue) {
            null -> null
            is String ->
                if (!jsonValue.toLowerCase().startsWith("0x")) throw JsonToAvroException("Invalid $jsonValue, BYTES value need to start with 0x")
                else ByteBuffer.wrap(DatatypeConverter.parseHexBinary(jsonValue.substring(2)))
            else -> throw JsonToAvroException("Expecting binary number but got $jsonValue")
        }
    }

    private fun parseFloat(fieldSchema: Schema, jsonValue: Any?) =
        when (jsonValue) {
            is Double -> jsonValue.toFloat()
            is Float -> jsonValue
            else -> throw JsonToAvroException("Expecting float but got $jsonValue")
        }
}

class JsonToAvroException(message: String, val nextField: String? = null) : Throwable(message)
class InvalidJsonException(message: String? = null) : Throwable(message)
