package insulator.jsonhelper.jsontoavro.fieldparser

import arrow.core.left
import arrow.core.right
import insulator.jsonhelper.jsontoavro.JsonFieldParser
import insulator.jsonhelper.jsontoavro.JsonInvalidFieldException
import insulator.jsonhelper.jsontoavro.jsonFieldParser
import org.apache.avro.generic.GenericData

data class SimpleTypeParsers(
    val intParser: JsonFieldParser<Int>,
    val longParser: JsonFieldParser<Long>,
    val floatParser: JsonFieldParser<Float>,
    val doubleParser: JsonFieldParser<Double>,
    val nullParser: JsonFieldParser<Any?>,
    val booleanParser: JsonFieldParser<Boolean>,
    val enumParser: JsonFieldParser<Any>,
    val stringParser: JsonFieldParser<String>,
)

class SimpleTypeParsersFactory {

    fun build() = SimpleTypeParsers(intParser, longParser, floatParser, doubleParser, nullParser, booleanParser, enumParser, stringParser)

    private val intParser = jsonFieldParser { field, schema ->
        if (field is Int) field.right()
        else JsonInvalidFieldException(schema, field).left()
    }

    private val longParser = jsonFieldParser { field, schema ->
        when (field) {
            is Long -> field.right()
            is Int -> field.toLong().right()
            else -> JsonInvalidFieldException(schema, field).left()
        }
    }

    private val floatParser = jsonFieldParser { field, schema ->
        when (field) {
            is Float -> field.right()
            is Double -> field.toFloat().right()
            else -> JsonInvalidFieldException(schema, field).left()
        }
    }

    private val doubleParser = jsonFieldParser { field, schema ->
        when (field) {
            is Double -> field.right()
            is Float -> field.toDouble().right()
            else -> JsonInvalidFieldException(schema, field).left()
        }
    }

    private val nullParser = jsonFieldParser { field, schema ->
        if (field == null) field.right()
        else JsonInvalidFieldException(schema, field).left()
    }

    private val booleanParser = jsonFieldParser { field, schema ->
        when (field) {
            is Boolean -> field.right()
            is String -> when (field.toLowerCase()) {
                "false" -> false.right()
                "true" -> true.right()
                else -> JsonInvalidFieldException(schema, field).left()
            }
            else -> JsonInvalidFieldException(schema, field).left()
        }
    }

    private val stringParser = jsonFieldParser { field, schema ->
        if (field is String) field.right()
        else JsonInvalidFieldException(schema, field).left()
    }

    private val enumParser = jsonFieldParser { field, schema ->
        val symbols = schema.enumSymbols
        if (field == null || field.toString() !in symbols) JsonInvalidFieldException(schema, field).left()
        else GenericData.EnumSymbol(schema, field).right()
    }
}
