package insulator.viewmodel.main.schemaregistry

import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.*

class ListSchemaViewModel : ViewModel() {
    private val schemaRegistryClient: SchemaRegistryClient by di()
    fun listSchemas(): ObservableList<String> {
        return FXCollections.observableArrayList(schemaRegistryClient.allSubjects)
    }

}