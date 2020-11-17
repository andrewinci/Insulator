package insulator.jsonhelper

import insulator.helper.runCatchingE
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

class JsonFormatter {

    private val json = Json {}

    fun formatJsonString(jsonString: String, indent: Boolean = true) = json.runCatchingE { parseToJsonElement(jsonString) }
        .map { InternalFormatter(indent).format(it, 1) }

    private class InternalFormatter(private val indent: Boolean = true) {
        @OptIn(ExperimentalStdlibApi::class)
        fun format(json: JsonElement, level: Int = 1): Collection<Token> {
            val newLine = if (indent) Token.Symbol("\n") else Token.Symbol(" ")
            return when (json) {
                is JsonPrimitive -> listOf(Token.Value(json.toString()))
                is JsonObject -> parseObject(newLine, level, json)
                is JsonArray -> parseArray(json, level)
                else -> if (json is JsonNull) listOf(Token.Value("null")) else throw Exception("Unable to parse")
            }
        }

        private fun parseObject(newLine: Token.Symbol, level: Int, json: JsonObject): List<Token> =
            listOf(Token.Symbol("{"), newLine, indent(level))
                .asSequence()
                .plus(
                    json.entries
                        .map { (key, value) -> listOf(Token.Key(key), Token.COLON).plus(format(value, level + 1)) }
                        .reduceOrNull { a, b -> a.plus(Token.COMMA).plus(newLine).plus(indent(level)).plus(b) }
                        ?: emptyList()
                )
                .plus(newLine).plus(indent(level - 1)).plus(Token.Symbol("}")).toList()

        private fun parseArray(json: JsonArray, level: Int): List<Token> =
            listOf(Token.Symbol("["))
                .plus(
                    json.map { format(it, level + 1) }
                        .reduceOrNull { a, b -> a.plus(Token.COMMA).plus(b) }
                        ?: emptyList()
                )
                .plus(Token.Symbol("]"))

        private fun indent(value: Int): Token {
            return if (indent) Token.Symbol("".padStart(value * 2, ' ')) else Token.Symbol("")
        }
    }
}

sealed class Token(val text: String) {
    data class Symbol(val t: String) : Token(t)
    data class Key(val t: String) : Token("\"$t\"")
    data class Value(val t: String) : Token(t)
    companion object {
        val COMMA = Symbol(", ")
        val COLON = Symbol(": ")
    }
}
