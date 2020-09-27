package insulator.lib.jsonhelper.jsontoavro.fieldparser

import arrow.core.left
import arrow.core.right
import insulator.lib.jsonhelper.jsontoavro.JsonFieldParser
import insulator.lib.jsonhelper.jsontoavro.JsonInvalidFieldException
import insulator.lib.jsonhelper.jsontoavro.jsonFieldParser
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
    fun build() = SimpleTypeParsers(
        intParser = jsonFieldParser { field, schema ->
            if (field is Int) field.right()
            else JsonInvalidFieldException(schema, field).left()
        },
        longParser = jsonFieldParser { field, schema ->
            when (field) {
                is Long -> field.right()
                is Int -> field.toLong().right()
                else -> JsonInvalidFieldException(schema, field).left()
            }
        },
        floatParser = jsonFieldParser { field, schema ->
            when (field) {
                is Float -> field.right()
                is Double -> field.toFloat().right()
                else -> JsonInvalidFieldException(schema, field).left()
            }
        },
        doubleParser = jsonFieldParser { field, schema ->
            when (field) {
                is Double -> field.right()
                is Float -> field.toDouble().right()
                else -> JsonInvalidFieldException(schema, field).left()
            }
        },
        nullParser = jsonFieldParser { field, schema ->
            if (field == null) field.right()
            else JsonInvalidFieldException(schema, field).left()
        },
        booleanParser = jsonFieldParser { field, schema ->
            when (field) {
                is Boolean -> field.right()
                (field as? String)?.toLowerCase() == "false" -> false.right()
                (field as? String)?.toLowerCase() == "true" -> true.right()
                else -> JsonInvalidFieldException(schema, field).left()
            }
        },
        stringParser = jsonFieldParser { field, schema ->
            if (field is String) field.right()
            else JsonInvalidFieldException(schema, field).left()
        },
        enumParser = jsonFieldParser { field, schema ->
            val symbols = schema.enumSymbols
            if (field == null || field.toString() !in symbols) JsonInvalidFieldException(schema, field).left()
            else GenericData.EnumSymbol(schema, field).right()
        }
    )
}