package insulator.lib.jsonhelper.jsontoavro.fieldparser

import insulator.lib.jsonhelper.jsontoavro.FieldParser
import insulator.lib.jsonhelper.jsontoavro.JsonFieldParser
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
