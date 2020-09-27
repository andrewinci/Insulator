package insulator.lib.jsonhelper.jsontoavro

import arrow.core.left
import insulator.lib.jsonhelper.jsontoavro.fieldparser.ComplexTypeParsersFactory
import insulator.lib.jsonhelper.jsontoavro.fieldparser.SimpleTypeParsersFactory
import org.apache.avro.Schema

class FieldParser(
    simpleTypeParsersFactory: SimpleTypeParsersFactory,
    complexTypeParsersFactory: ComplexTypeParsersFactory
) {

    private val simpleTypeParsers = simpleTypeParsersFactory.build()
    private val complexTypeParsers = complexTypeParsersFactory.build(this)

    fun parseField(jsonValue: Any?, fieldSchema: Schema) = when (fieldSchema.type) {
        // composed types
        Schema.Type.RECORD -> complexTypeParsers.recordParser
        Schema.Type.ARRAY -> complexTypeParsers.arrayParser
        Schema.Type.UNION -> complexTypeParsers.unionParser
        Schema.Type.BYTES -> complexTypeParsers.byteParser
        Schema.Type.FIXED -> jsonFieldParser { _, _ -> JsonFieldParsingException("Avro FIXED type not supported").left() }
        Schema.Type.MAP -> jsonFieldParser { _, _ -> JsonFieldParsingException("Avro MAP type not supported").left() }

        // simple types
        Schema.Type.STRING -> simpleTypeParsers.stringParser
        Schema.Type.ENUM -> simpleTypeParsers.enumParser
        Schema.Type.INT -> simpleTypeParsers.intParser
        Schema.Type.LONG -> simpleTypeParsers.longParser
        Schema.Type.FLOAT -> simpleTypeParsers.floatParser
        Schema.Type.DOUBLE -> simpleTypeParsers.doubleParser
        Schema.Type.BOOLEAN -> simpleTypeParsers.booleanParser
        Schema.Type.NULL -> simpleTypeParsers.nullParser
        else -> jsonFieldParser { _, _ -> JsonFieldParsingException("Null schema type").left() }
    }.parse(jsonValue, fieldSchema)
}
