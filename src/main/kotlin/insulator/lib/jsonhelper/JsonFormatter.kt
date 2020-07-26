package insulator.lib.jsonhelper

import arrow.core.left
import arrow.core.right
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import java.lang.Exception

class JsonFormatter(private val gson: Gson) {

    fun formatJsonString(json: String) = kotlin.runCatching { gson.fromJson(json, JsonObject::class.java) }
        .map { format(it) }
        .fold({ it.right() }, { it.left() })

    private fun format(json: JsonElement, indentCount: Int = 1): Collection<Token> {
        val indent = Token.Symbol("".padStart(indentCount * 2, ' '))
        return when {
            json.isJsonPrimitive -> listOf(Token.Value(json.asJsonPrimitive.toString()))
            json.isJsonObject ->
                listOf(Token.Symbol("{"), Token.NEWLINE, indent)
                    .plus(
                        json.asJsonObject.entrySet()
                            .map { (key, value) -> listOf(Token.Key(key), Token.COLON).plus(format(value, indentCount + 1)) }
                            .reduce { a, b -> a.plus(Token.COMMA).plus(Token.NEWLINE).plus(indent).plus(b) }
                    )
                    .plus(Token.NEWLINE).plus(indent).plus(Token.Symbol("}")).toList()

            json.isJsonArray ->
                listOf(Token.Symbol("["))
                    .plus(json.asJsonArray.map { format(it, indentCount + 1) }.reduce { a, b -> a.plus(Token.COMMA).plus(b) })
                    .plus(Token.Symbol("]"))
            json.isJsonNull -> listOf(Token.Value("null"))
            else -> throw Exception("Unable to parse")
        }
    }
}

sealed class Token(val text: String, val color: Color, val fontWeight: FontWeight) {
    data class Symbol(val t: String) : Token(t, Color.GRAY, FontWeight.LIGHT)
    data class Key(val t: String) : Token("\"$t\"", Color.BLUE, FontWeight.EXTRA_BOLD)
    data class Value(val t: String) : Token(t, Color.GREEN, FontWeight.NORMAL)
    companion object {
        val COMMA = Symbol(", ")
        val COLON = Symbol(": ")
        val NEWLINE = Symbol("\n")
    }
}
