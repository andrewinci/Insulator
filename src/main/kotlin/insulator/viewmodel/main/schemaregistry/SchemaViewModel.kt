package insulator.viewmodel.main.schemaregistry

import insulator.lib.jsonhelper.JsonFormatter
import insulator.lib.jsonhelper.Token
import insulator.lib.kafka.SchemaRegistry
import insulator.lib.kafka.model.Subject
import insulator.viewmodel.common.InsulatorViewModel
import javafx.beans.property.Property
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.input.Clipboard
import tornadofx.* // ktlint-disable no-wildcard-imports

class SchemaViewModel(val schema: Subject) : InsulatorViewModel() {
    private val formatter: JsonFormatter by di()
    private val schemaRegistry: SchemaRegistry by di()

    val nameProperty = SimpleStringProperty(schema.subject)
    val versionsProperty: ObservableList<Int> = FXCollections.observableArrayList<Int>(schema.schemas.map { it.version })
    val selectedVersionProperty: Property<Int> = SimpleObjectProperty<Int>(-1)
    val schemaProperty: ObservableList<Token> = FXCollections.observableArrayList<Token>()

    init {
        refresh()
    }

    private fun refresh() {
        selectedVersionProperty.onChange { version ->
            val schemaVersion = schema.schemas.first { it.version == version }.schema
            val res = formatter.formatJsonString(schemaVersion)
            schemaProperty.clear()
            schemaProperty.addAll(res.fold({ listOf(Token.Value("Unable to parse ${it.message}")) }, { it }))
        }
        selectedVersionProperty.value = schema.schemas.last().version
    }

    fun delete() = schemaRegistry.deleteSubject(nameProperty.value)

    fun copySchemaToClipboard() {
        Clipboard.getSystemClipboard().putString(schemaProperty.joinToString(separator = "") { it.text })
    }
}
