package insulator.viewmodel.main.schemaregistry

import insulator.lib.kafka.SchemaRegistry
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.*

class ListSchemaViewModel : ViewModel() {
    private val schemaRegistryClient: SchemaRegistry by di()
    fun listSchemas(): ObservableList<String> {
        return FXCollections.observableList(schemaRegistryClient.getAllSubjects().toList())
    }

    fun getSchema(subject: String) : SchemaViewModel {
        return SchemaViewModel(subject, schemaRegistryClient.getSchema(subject))
    }
}