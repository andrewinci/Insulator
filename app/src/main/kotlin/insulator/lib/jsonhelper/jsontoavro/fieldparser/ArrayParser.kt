package insulator.lib.jsonhelper.jsontoavro.fieldparser

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import insulator.lib.helpers.toEitherOfList
import insulator.lib.jsonhelper.jsontoavro.FieldParser
import insulator.lib.jsonhelper.jsontoavro.JsonFieldParser
import insulator.lib.jsonhelper.jsontoavro.JsonFieldParsingException
import insulator.lib.jsonhelper.jsontoavro.JsonInvalidFieldException
import org.apache.avro.Schema

class ArrayParser(private val fieldParser: FieldParser) : JsonFieldParser<List<Any?>> {
    override fun parse(fieldValue: Any?, schema: Schema): Either<JsonFieldParsingException, List<Any?>> {
        if (fieldValue !is Collection<*>) return JsonInvalidFieldException(schema, fieldValue).left()
        if (fieldValue.size == 0) return emptyList<Any?>().right()
        // field value is a non-empty list
        return fieldValue.map { fieldParser.parseField(it, schema.elementType) }.toEitherOfList()
    }
}
