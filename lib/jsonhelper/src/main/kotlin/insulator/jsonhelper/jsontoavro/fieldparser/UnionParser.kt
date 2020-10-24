package insulator.jsonhelper.jsontoavro.fieldparser

import arrow.core.Either
import arrow.core.left
import insulator.jsonhelper.jsontoavro.FieldParser
import insulator.jsonhelper.jsontoavro.JsonFieldParser
import insulator.jsonhelper.jsontoavro.JsonFieldParsingException
import org.apache.avro.Schema

class UnionParser(private val fieldParser: FieldParser) : JsonFieldParser<Any?> {
    override fun parse(fieldValue: Any?, schema: Schema): Either<JsonFieldParsingException, Any?> {
        val mapAttempts = schema.types.map { fieldParser.parseField(fieldValue, it) }
        return if (mapAttempts.any { it.isRight() }) mapAttempts.first { it.isRight() }
        else mapAttempts
            .filter { it.isLeft() }
            .map { (it as Either.Left<JsonFieldParsingException>).a }
            .map { it.message }
            .joinToString(separator = "\n")
            .let { JsonFieldParsingException(it).left() }
    }
}
