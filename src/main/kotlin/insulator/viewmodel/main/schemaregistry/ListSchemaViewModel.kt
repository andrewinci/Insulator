package insulator.viewmodel.main.schemaregistry

import arrow.core.extensions.either.applicativeError.handleError
import insulator.lib.configuration.model.Cluster
import insulator.lib.helpers.runOnFXThread
import insulator.lib.kafka.SchemaRegistry
import insulator.ui.common.scope
import insulator.viewmodel.common.InsulatorViewModel
import insulator.views.main.schemaregistry.SchemaView
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableStringValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.* // ktlint-disable no-wildcard-imports

class ListSchemaViewModel : InsulatorViewModel() {

    private val cluster: Cluster by di()
    private val schemaRegistryClient: SchemaRegistry by di()

    private val schemasProperty: ObservableList<String> = FXCollections.observableArrayList()

    val selectedSchemaProperty = SimpleStringProperty()
    val searchItemProperty = SimpleStringProperty()

    val filteredSchemasProperty = SortedFilteredList(schemasProperty).apply {
        filterWhen(searchItemProperty) { p, i -> i.toLowerCase().contains(p.toLowerCase()) }
    }.filteredItems

    val subtitleProperty: ObservableStringValue = Bindings.createStringBinding(
        {
            "Topic count: ${filteredSchemasProperty.size}/${schemasProperty.size}"
        },
        schemasProperty,
        filteredSchemasProperty
    )

    init {
        refresh()
    }

    fun refresh() = schemaRegistryClient
        .getAllSubjects()
        .map { it.sorted() }
        .map {
            it.runOnFXThread {
                schemasProperty.clear()
                schemasProperty.addAll(it)
            }
        }.handleError {
            error.set(LoadSchemaListError(it.message ?: "Unable to load the schema list"))
        }

    fun showSchema() {
        if (selectedSchemaProperty.value.isNullOrEmpty()) return
        schemaRegistryClient.getSubject(selectedSchemaProperty.value!!)
            .map { SchemaViewModel(it) }
            .fold(
                { error.set(LoadSchemaError(it.message ?: "Unable to load the schema")) },
                {
                    it.subject.scope(cluster)
                        .withComponent(it)
                        .let { schemaView -> find<SchemaView>(schemaView) }
                        .also { schemaViewTab -> schemaViewTab.whenUndockedOnce { refresh() } }
                        .let { schemaViewTab -> setMainContent(it.nameProperty.value, schemaViewTab) }
                }
            )
    }
}

class LoadSchemaError(message: String) : Throwable(message)
class LoadSchemaListError(message: String) : Throwable(message)
