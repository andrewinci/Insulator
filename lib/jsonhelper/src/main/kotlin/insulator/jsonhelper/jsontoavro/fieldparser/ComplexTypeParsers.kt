package insulator.jsonhelper.jsontoavro.fieldparser

import insulator.jsonhelper.jsontoavro.FieldParser
import insulator.jsonhelper.jsontoavro.JsonFieldParser
import org.apache.avro.generic.GenericRecord
import java.nio.ByteBuffer

data class ComplexTypeParsers(
    val arrayParser: JsonFieldParser<List<Any?>>,
    val recordParser: JsonFieldParser<GenericRecord>,
    val unionParser: JsonFieldParser<Any?>,
    val byteParser: JsonFieldParser<ByteBuffer>,
)

class ComplexTypeParsersFactory {
    fun build(fieldParser: FieldParser) = ComplexTypeParsers(
        recordParser = RecordParser(fieldParser),
        arrayParser = ArrayParser(fieldParser),
        unionParser = UnionParser(fieldParser),
        byteParser = ByteParser(),
    )
}
