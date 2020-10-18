package insulator.viewmodel.main.schemaregistry

import arrow.core.extensions.either.applicativeError.handleError
import insulator.di.dagger.ClusterScope
import insulator.di.dagger.components.SubjectComponent
import insulator.di.dagger.factories.Factory
import insulator.lib.configuration.model.Cluster
import insulator.lib.helpers.runOnFXThread
import insulator.lib.kafka.SchemaRegistry
import insulator.lib.kafka.model.Subject
import insulator.viewmodel.common.InsulatorViewModel
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableStringValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.* // ktlint-disable no-wildcard-imports
import javax.inject.Inject

@ClusterScope
class ListSchemaViewModel @Inject constructor(
    val cluster: Cluster,
    val schemaRegistryClient: SchemaRegistry,
    private val viewFactory: Factory<Subject, SubjectComponent>
) : InsulatorViewModel() {

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

    suspend fun showSchema() {
        if (selectedSchemaProperty.value.isNullOrEmpty()) return
        schemaRegistryClient.getSubject(selectedSchemaProperty.value!!)
            .map {
                viewFactory.build(it)
                    .getSchemaView()
                    .also { schemaViewTab -> schemaViewTab.whenUndockedOnce { refresh() } }
                    .let { schemaViewTab -> setMainContent(it.name, schemaViewTab) }
            }
            .mapLeft { error.set(LoadSchemaError(it.message ?: "Unable to load the schema")) }
    }
}

class LoadSchemaError(message: String) : Throwable(message)
class LoadSchemaListError(message: String) : Throwable(message)
