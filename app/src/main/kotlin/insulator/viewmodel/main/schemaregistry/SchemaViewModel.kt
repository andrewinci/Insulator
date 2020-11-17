package insulator.viewmodel.main.schemaregistry

import insulator.helper.createListBindings
import insulator.kafka.SchemaRegistry
import insulator.kafka.model.Cluster
import insulator.kafka.model.Schema
import insulator.kafka.model.Subject
import insulator.viewmodel.common.InsulatorViewModel
import javafx.beans.binding.Bindings
import javafx.beans.property.Property
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableStringValue
import javafx.collections.ObservableList
import tornadofx.setValue
import javax.inject.Inject

class SchemaViewModel @Inject constructor(
    val cluster: Cluster,
    private val subject: Subject,
    private val schemaRegistry: SchemaRegistry?,
) : InsulatorViewModel() {

    private val subjectProperty = SimpleObjectProperty(subject)
    val nameProperty: ObservableStringValue = Bindings.createStringBinding({ subjectProperty.value.name }, subjectProperty)
    val versionsProperty: ObservableList<Schema> = createListBindings({ subjectProperty.value.schemas }, subjectProperty)
    val selectedVersionProperty: Property<Schema> = SimpleObjectProperty(subjectProperty.value.schemas.last())
    val schemaProperty: ObservableStringValue = Bindings.createStringBinding({ selectedVersionProperty.value?.schema ?: "" }, selectedVersionProperty)

    suspend fun refresh() {
        schemaRegistry!!.getSubject(subject.name)
            .map { subject ->
                subjectProperty.set(subject)
                selectedVersionProperty.value = subject.schemas.last()
            }
    }

    fun delete() = schemaRegistry?.deleteSubject(nameProperty.value)
}
