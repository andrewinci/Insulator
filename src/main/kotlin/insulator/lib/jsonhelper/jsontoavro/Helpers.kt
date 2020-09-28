package insulator.lib.jsonhelper.jsontoavro

import arrow.core.Either
import org.apache.avro.Schema

fun <O> jsonFieldParser(parseFn: (fieldValue: Any?, schema: Schema) -> Either<JsonFieldParsingException, O>): JsonFieldParser<O> = object : JsonFieldParser<O> {
    override fun parse(fieldValue: Any?, schema: Schema) = parseFn(fieldValue, schema)
}

internal fun Schema.printType(): String {
    return when (this.type) {
        Schema.Type.NULL -> "null"
        Schema.Type.RECORD -> "record \"${this.toString(true)}\""
        Schema.Type.BYTES -> "bytes (eg \"0x00\")"
        Schema.Type.ENUM -> "enum [${this.enumSymbols.joinToString(", ")}]"
        Schema.Type.UNION -> "union [${this.types.joinToString(", ") { it.toString(true) }}]"
        Schema.Type.ARRAY -> "array of \"${this.elementType.toString(true)}\""
        else -> this.type.name.toLowerCase()
    }
}
