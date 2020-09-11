package insulator.viewmodel.main.schemaregistry

import insulator.lib.helpers.runOnFXThread
import insulator.lib.kafka.SchemaRegistry
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.ViewModel

class ListSchemaViewModel : ViewModel() {

    private val schemaRegistryClient: SchemaRegistry by di()

    val listSchema: ObservableList<String> = FXCollections.observableArrayList<String>()

    init { refresh() }

    fun getSchema(subject: String) = schemaRegistryClient.getSubject(subject)
        .map { SchemaViewModel(it) }
        .fold({ throw it }, { it })

    fun refresh() = schemaRegistryClient
        .getAllSubjects()
        .map { it.sorted() }
        .map {
            it.runOnFXThread {
                listSchema.clear()
                listSchema.addAll(it)
            }
        }
}
