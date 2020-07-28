package insulator.viewmodel.main.schemaregistry

import insulator.lib.kafka.SchemaRegistry
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.ViewModel

class ListSchemaViewModel : ViewModel() {

    private val schemaRegistryClient: SchemaRegistry by di()

    fun listSchemas(): ObservableList<String> = schemaRegistryClient.getAllSubjects()
        .map { FXCollections.observableList(it.toList()) }
        .fold({ throw it }, { it })

    fun getSchema(subject: String) = schemaRegistryClient.getSubject(subject)
        .map { SchemaViewModel(it) }
        .fold({ throw it }, { it })
}
