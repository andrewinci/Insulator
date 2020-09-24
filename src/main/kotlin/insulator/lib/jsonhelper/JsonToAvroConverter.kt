package insulator.lib.jsonhelper

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.avro.Conversions
import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericRecord
import org.apache.avro.generic.GenericRecordBuilder
import java.nio.ByteBuffer
import javax.xml.bind.DatatypeConverter

class JsonToAvroConverter() {

    fun convert(jsonString: String, schemaString: String): Either<Throwable, GenericRecord> {
        return try {
            val jsonMap = ObjectMapper().readValue(jsonString, Map::class.java)
            val schema = Schema.Parser().parse(schemaString)
            val parsed = parseRecord(schema, jsonMap)
            if (GenericData().validate(schema, parsed)) {
                parsed.right()
            } else Throwable("Invalid data").left()
        } catch (ex: Throwable) {
            ex.left()
        }
    }

    private fun parseRecord(schema: Schema, jsonMap: Map<*, *>?): GenericRecord {
        if (schema.type != Schema.Type.RECORD || jsonMap == null) throw Throwable("Invalid json")
        val recordBuilder = GenericRecordBuilder(schema)
        schema.fields.forEach { fieldSchema ->
            val fieldName = fieldSchema.name()
            val jsonValue = jsonMap[fieldName]
            recordBuilder.set(fieldSchema, parseField(fieldSchema.schema(), jsonValue))
        }
        return recordBuilder.build()
    }

    private fun parseField(fieldSchema: Schema, jsonValue: Any?): Any? {
        return when (fieldSchema.type) {
            Schema.Type.NULL -> if (jsonValue == null) null else throw Throwable("$jsonValue should be NULL")
            Schema.Type.RECORD -> parseRecord(fieldSchema, jsonValue as? Map<*, *>)
            Schema.Type.FLOAT -> parseFloat(jsonValue)
            Schema.Type.BYTES -> parseBytes(fieldSchema, jsonValue)
            Schema.Type.ENUM -> parseEnum(fieldSchema, jsonValue)
            Schema.Type.UNION -> parseUnion(fieldSchema, jsonValue)
            else -> jsonValue
        }
    }

    private fun parseUnion(fieldSchema: Schema, jsonValue: Any?): Any? {
        fieldSchema.types.forEach {
            val parsed = kotlin.runCatching { parseField(it, jsonValue) }
            if (parsed.isSuccess) return parsed.getOrNull()
        }
        throw Throwable("Unable to parse $jsonValue to type $fieldSchema")
    }

    private fun parseEnum(fieldSchema: Schema, jsonValue: Any?) =
        if (jsonValue == null) null
        else GenericData.EnumSymbol(fieldSchema, jsonValue)

    private fun parseBytes(fieldSchema: Schema, jsonValue: Any?): ByteBuffer? {
        if (jsonValue == null) return null
        if (jsonValue is Double && fieldSchema.logicalType.name == "decimal")
            return Conversions.DecimalConversion().toBytes(jsonValue.toBigDecimal(), fieldSchema, fieldSchema.logicalType)
        return when (jsonValue) {
            null -> null
            is String ->
                if (!jsonValue.toLowerCase().startsWith("0x")) throw Throwable("Invalid $jsonValue, BYTES value need to start with 0x")
                else ByteBuffer.wrap(DatatypeConverter.parseHexBinary(jsonValue.substring(2)))
            else -> throw Throwable("Invalid binary number $jsonValue")
        }
    }

    private fun parseFloat(jsonValue: Any?) =
        when (jsonValue) {
            null -> null
            is Double -> jsonValue.toFloat()
            is Float -> jsonValue
            else -> throw Throwable("Invalid float number $jsonValue")
        }
}
