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
import org.apache.avro.generic.GenericRecord
import scala.util.Left
import java.nio.ByteBuffer

data class ComplexTypeParsers(
    val arrayParser: JsonFieldParser<List<Any?>>,
    val recordParser: JsonFieldParser<GenericRecord>,
    val unionParser: JsonFieldParser<Any?>,
    val byteParser: JsonFieldParser<ByteBuffer>,
)

class ComplexTypeParsersFactory() {
    fun build(fieldParser: FieldParser) = ComplexTypeParsers(
        recordParser = RecordParser(fieldParser),
        arrayParser = ArrayParser(fieldParser),
        unionParser = UnionParser(fieldParser),
        byteParser = ByteParser(),
    )
}

class ArrayParser(private val fieldParser: FieldParser) : JsonFieldParser<List<Any?>> {
    override fun parse(fieldValue: Any?, schema: Schema): Either<JsonFieldParsingException, List<Any?>> {
        if (fieldValue !is ArrayList<*>) return JsonInvalidFieldException(schema, fieldValue).left()
        if (fieldValue.size == 0) return emptyList<Any?>().right()
        // field value is a non-empty list
        return fieldValue.map { fieldParser.parseField(it, schema.elementType) }.toEitherOfList()
    }
}

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
