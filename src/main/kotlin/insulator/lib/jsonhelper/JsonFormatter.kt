package insulator.lib.jsonhelper

import arrow.core.left
import arrow.core.right
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.lang.Exception

class JsonFormatter(private val gson: Gson) {

    fun formatJsonString(json: String, indent: Boolean = true) = kotlin.runCatching { gson.fromJson(json, JsonObject::class.java) }
        .map { InternalFormatter(indent).format(it, 1) }
        .fold({ it.right() }, { it.left() })

    private class InternalFormatter(private val indent: Boolean = true) {
        @OptIn(ExperimentalStdlibApi::class)
        fun format(json: JsonElement, level: Int = 1): Collection<Token> {
            val newLine = if (indent) Token.Symbol("\n") else Token.Symbol(" ")
            return when {
                json.isJsonPrimitive -> listOf(Token.Value(json.asJsonPrimitive.toString()))
                json.isJsonObject ->
                    listOf(Token.Symbol("{"), newLine, indent(level))
                        .asSequence()
                        .plus(
                            json.asJsonObject.entrySet()
                                .map { (key, value) -> listOf(Token.Key(key), Token.COLON).plus(format(value, level + 1)) }
                                .reduceOrNull { a, b -> a.plus(Token.COMMA).plus(newLine).plus(indent(level)).plus(b) }
                                ?: emptyList()
                        )
                        .plus(newLine).plus(indent(level - 1)).plus(Token.Symbol("}")).toList()

                json.isJsonArray ->
                    listOf(Token.Symbol("["))
                        .plus(json.asJsonArray.map { format(it, level + 1) }.reduce { a, b -> a.plus(Token.COMMA).plus(b) })
                        .plus(Token.Symbol("]"))
                json.isJsonNull -> listOf(Token.Value("null"))
                else -> throw Exception("Unable to parse")
            }
        }

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
