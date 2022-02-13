package insulator.jsonhelper.jsontoavro.fieldparser

import arrow.core.Left
import arrow.core.Right
import arrow.core.computations.either
import arrow.core.left
import arrow.core.right
import insulator.jsonhelper.jsontoavro.FieldParser
import insulator.jsonhelper.jsontoavro.JsonFieldParser
import insulator.jsonhelper.jsontoavro.JsonFieldParsingException
import insulator.jsonhelper.jsontoavro.JsonInvalidFieldException
import insulator.jsonhelper.jsontoavro.JsonMissingFieldException
import insulator.jsonhelper.jsontoavro.JsonUnexpectedFieldException
import org.apache.avro.Schema
import org.apache.avro.generic.GenericRecord
import org.apache.avro.generic.GenericRecordBuilder

internal class RecordParser(private val fieldParser: FieldParser) : JsonFieldParser<GenericRecord> {

    override fun parse(fieldValue: Any?, schema: Schema) = either.eager<JsonFieldParsingException, GenericRecord> {
        val jsonMap = (
            if (schema.type != Schema.Type.RECORD || fieldValue !is Map<*, *>)
                JsonInvalidFieldException(schema, fieldValue).left()
            else fieldValue.right()
            ).map { it.toMutableMap() }.bind()

        val recordBuilder = GenericRecordBuilder(schema)
        schema.fields.forEach { fieldSchema ->
            with(fieldSchema.name()) {
                val parsedField = (
                    if (this !in jsonMap) JsonMissingFieldException(fieldSchema.schema(), this).left()
                    else fieldParser.parseField(jsonMap[this], fieldSchema.schema())
                    ).bind()
                recordBuilder.set(fieldSchema, parsedField)
                jsonMap.remove(this)
            }
        }
        !(
            if (jsonMap.isEmpty()) Right(recordBuilder.build())
            else Left(JsonUnexpectedFieldException(jsonMap.keys.toList()))
            )
    }
}
