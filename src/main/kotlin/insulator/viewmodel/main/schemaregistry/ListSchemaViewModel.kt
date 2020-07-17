package insulator.viewmodel.main.schemaregistry

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.*

class ListSchemaViewModel : ViewModel() {

    fun listSchemas(): ObservableList<String> = FXCollections.observableArrayList(listOf("Schema1"))

}