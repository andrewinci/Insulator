package insulator.jsonhelper.jsontoavro

import arrow.core.left
import insulator.jsonhelper.jsontoavro.fieldparser.ComplexTypeParsersFactory
import insulator.jsonhelper.jsontoavro.fieldparser.SimpleTypeParsersFactory
import org.apache.avro.Schema

class FieldParser(
    simpleTypeParsersFactory: SimpleTypeParsersFactory,
    complexTypeParsersFactory: ComplexTypeParsersFactory
) {

    private val simpleTypeParsers = simpleTypeParsersFactory.build()
    private val complexTypeParsers = complexTypeParsersFactory.build(this)
    private val parsersLookup = mapOf(
        // composed types
        Schema.Type.RECORD to complexTypeParsers.recordParser,
        Schema.Type.ARRAY to complexTypeParsers.arrayParser,
        Schema.Type.UNION to complexTypeParsers.unionParser,
        Schema.Type.BYTES to complexTypeParsers.byteParser,
        Schema.Type.FIXED to jsonFieldParser { _, _ -> JsonFieldParsingException("Avro FIXED type not supported").left() },
        Schema.Type.MAP to jsonFieldParser { _, _ -> JsonFieldParsingException("Avro MAP type not supported").left() },

        // simple types
        Schema.Type.STRING to simpleTypeParsers.stringParser,
        Schema.Type.ENUM to simpleTypeParsers.enumParser,
        Schema.Type.INT to simpleTypeParsers.intParser,
        Schema.Type.LONG to simpleTypeParsers.longParser,
        Schema.Type.FLOAT to simpleTypeParsers.floatParser,
        Schema.Type.DOUBLE to simpleTypeParsers.doubleParser,
        Schema.Type.BOOLEAN to simpleTypeParsers.booleanParser,
        Schema.Type.NULL to simpleTypeParsers.nullParser,
    )

    fun parseField(jsonValue: Any?, fieldSchema: Schema) = (
        parsersLookup[fieldSchema.type]
            ?: jsonFieldParser { _, _ -> JsonFieldParsingException("Null schema type").left() }
        ).parse(jsonValue, fieldSchema)
}
