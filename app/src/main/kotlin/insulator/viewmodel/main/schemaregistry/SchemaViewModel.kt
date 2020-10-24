package insulator.viewmodel.main.schemaregistry

import insulator.jsonhelper.JsonFormatter
import insulator.jsonhelper.Token
import insulator.kafka.SchemaRegistry
import insulator.kafka.model.Cluster
import insulator.kafka.model.Schema
import insulator.kafka.model.Subject
import insulator.viewmodel.common.InsulatorViewModel
import javafx.beans.property.Property
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.input.Clipboard
import tornadofx.onChange
import tornadofx.putString
import javax.inject.Inject

class SchemaViewModel @Inject constructor(
    val cluster: Cluster,
    val subject: Subject,
    private val formatter: JsonFormatter,
    private val schemaRegistry: SchemaRegistry,
) : InsulatorViewModel() {

    val nameProperty = SimpleStringProperty(subject.name)
    val versionsProperty: ObservableList<Schema> = FXCollections.observableArrayList(subject.schemas)
    val selectedVersionProperty: Property<Schema> = SimpleObjectProperty(null)
    val schemaProperty: ObservableList<Token> = FXCollections.observableArrayList()

    init {
        refresh()
    }

    private fun refresh() {
        selectedVersionProperty.onChange {
            val schemaVersion = selectedVersionProperty.value.schema
            val res = formatter.formatJsonString(schemaVersion)
            schemaProperty.clear()
            schemaProperty.addAll(res.fold({ listOf(Token.Value("Unable to parse ${it.message}")) }, { it }))
        }
        selectedVersionProperty.value = subject.schemas.last()
    }

    fun delete() = schemaRegistry.deleteSubject(nameProperty.value)

    fun copySchemaToClipboard() {
        Clipboard.getSystemClipboard().putString(schemaProperty.joinToString(separator = "") { it.text })
    }
}
