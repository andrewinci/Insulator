package insulator.viewmodel.main.schemaregistry

import insulator.kafka.SchemaRegistry
import insulator.kafka.model.Cluster
import insulator.kafka.model.Schema
import insulator.kafka.model.Subject
import insulator.viewmodel.common.InsulatorViewModel
import javafx.beans.binding.Bindings
import javafx.beans.property.Property
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableStringValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javax.inject.Inject

class SchemaViewModel @Inject constructor(
    val cluster: Cluster,
    private val subject: Subject,
    private val schemaRegistry: SchemaRegistry?,
) : InsulatorViewModel() {

    val nameProperty = SimpleStringProperty(subject.name)
    val versionsProperty: ObservableList<Schema> = FXCollections.observableArrayList(subject.schemas)
    val selectedVersionProperty: Property<Schema> = SimpleObjectProperty(subject.schemas.last())
    val schemaProperty: ObservableStringValue = Bindings.createStringBinding({ selectedVersionProperty.value.schema }, selectedVersionProperty)

    suspend fun refresh() {
        schemaRegistry!!.getSubject(subject.name)
            .map { subject ->
                nameProperty.set(subject.name)
                selectedVersionProperty.value = subject.schemas.last()
                versionsProperty.clear()
                versionsProperty.setAll(subject.schemas)
            }
    }

    fun delete() = schemaRegistry?.deleteSubject(nameProperty.value)
}
