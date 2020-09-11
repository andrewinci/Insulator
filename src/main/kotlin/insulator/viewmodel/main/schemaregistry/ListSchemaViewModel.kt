package insulator.viewmodel.main.schemaregistry

import arrow.core.extensions.either.applicativeError.handleError
import insulator.lib.helpers.runOnFXThread
import insulator.lib.kafka.SchemaRegistry
import insulator.viewmodel.common.InsulatorViewModel
import javafx.collections.FXCollections
import javafx.collections.ObservableList

class ListSchemaViewModel : InsulatorViewModel() {

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
        }.handleError {
            error.set(it)
        }
}
