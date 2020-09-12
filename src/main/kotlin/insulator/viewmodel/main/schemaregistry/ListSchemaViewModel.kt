package insulator.viewmodel.main.schemaregistry

import arrow.core.extensions.either.applicativeError.handleError
import insulator.lib.helpers.runOnFXThread
import insulator.lib.kafka.SchemaRegistry
import insulator.viewmodel.common.InsulatorViewModel
import insulator.views.main.schemaregistry.SchemaView
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.*

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
                    val scope = Scope()
                    tornadofx.setInScope(it, scope)
                    find<SchemaView>(scope)
                        .also { view -> view.whenUndockedOnce { refresh() } }
                        .openWindow()
                })
    }
}

class LoadSchemaError(message: String) : Throwable(message)
class LoadSchemaListError(message: String) : Throwable(message)
