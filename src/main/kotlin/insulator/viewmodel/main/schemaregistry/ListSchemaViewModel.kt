package insulator.viewmodel.main.schemaregistry

import arrow.core.extensions.either.applicativeError.handleError
import insulator.lib.helpers.runOnFXThread
import insulator.lib.kafka.SchemaRegistry
import insulator.viewmodel.common.InsulatorViewModel
import insulator.views.common.StringScope
import insulator.views.main.schemaregistry.SchemaView
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.* // ktlint-disable no-wildcard-imports

class ListSchemaViewModel : InsulatorViewModel() {

    private val schemaRegistryClient: SchemaRegistry by di()

    val selectedSchema = SimpleStringProperty()
    val listSchema: ObservableList<String> = FXCollections.observableArrayList<String>()

    init {
        refresh()
    }

    fun refresh() = schemaRegistryClient
        .getAllSubjects()
        .map { it.sorted() }
        .map {
            it.runOnFXThread {
                listSchema.clear()
                listSchema.addAll(it)
            }
        }.handleError {
            error.set(LoadSchemaListError(it.message ?: "Unable to load the schema list"))
        }

    fun showSchema() {
        if (selectedSchema.value.isNullOrEmpty()) return
        schemaRegistryClient.getSubject(selectedSchema.value!!)
            .map { SchemaViewModel(it) }
            .fold(
                { error.set(LoadSchemaError(it.message ?: "Unable to load the schema")) },
                {
                    StringScope(it.nameProperty.value)
                        .withComponent(it)
                        .let {
                            find<SchemaView>(it)
                                .also { view -> view.whenUndockedOnce { refresh() } }
                                .openWindow()
                        }
                }
            )
    }
}

class LoadSchemaError(message: String) : Throwable(message)
class LoadSchemaListError(message: String) : Throwable(message)
