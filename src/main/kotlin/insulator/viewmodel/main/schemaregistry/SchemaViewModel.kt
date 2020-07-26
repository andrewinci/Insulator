package insulator.viewmodel.main.schemaregistry

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import insulator.lib.kafka.model.Subject
import javafx.beans.property.Property
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*
import java.lang.Exception


class SchemaViewModel(schema: Subject) : ViewModel() {

    private val gson: Gson by di()

    val nameProperty = SimpleStringProperty(schema.subject)
    val versionsProperty = FXCollections.observableArrayList<Int>(schema.schemas.map { it.version })
    val selectedVersionProperty : Property<Int> = SimpleObjectProperty<Int>(-1)
    val schemaProperty: ObservableList<Token> = FXCollections.observableArrayList<Token>()

    init {
        selectedVersionProperty.onChange { version ->
            val schemaVersion = schema.schemas.first { it.version == version }.schema

            val res = kotlin.runCatching { gson.fromJson(schemaVersion, JsonObject::class.java) }
                    .map { format(it) }
            schemaProperty.clear()
            schemaProperty.addAll(res.fold({ it }, { listOf(Token.Value("Unable to parse ${it.message}")) }))
        }
        selectedVersionProperty.value = schema.schemas.last().version
    }

    private fun format(json: JsonElement, indentCount: Int = 1): Collection<Token> {
        val indent = Token.Symbol("".padStart(indentCount * 2, ' '))
        return when {
            json.isJsonPrimitive -> listOf(Token.Value(json.asJsonPrimitive.toString()))
            json.isJsonObject -> listOf(Token.Symbol("{"), Token.NEWLINE, indent)
                    .plus(json.asJsonObject.entrySet()
                            .map { (key, value) -> listOf(Token.Key(key), Token.COLON).plus(format(value, indentCount + 1)) }
                            .reduce { a, b -> a.plus(Token.COMMA).plus(Token.NEWLINE).plus(indent).plus(b) }
                    )
                    .plus(Token.NEWLINE).plus(indent).plus(Token.Symbol("}")).toList()

            json.isJsonArray -> listOf(Token.Symbol("["))
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