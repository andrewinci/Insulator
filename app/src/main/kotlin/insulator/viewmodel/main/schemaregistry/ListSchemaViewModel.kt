package insulator.viewmodel.main.schemaregistry

import insulator.di.ClusterScope
import insulator.di.SubjectComponentFactory
import insulator.helper.dispatch
import insulator.helper.runOnFXThread
import insulator.kafka.SchemaRegistry
import insulator.kafka.model.Cluster
import insulator.kafka.model.Subject
import insulator.ui.WindowsManager
import insulator.viewmodel.common.InsulatorViewModel
import insulator.viewmodel.main.TabViewModel
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableStringValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.stage.Window
import tornadofx.SortedFilteredList
import tornadofx.whenUndockedOnce
import javax.inject.Inject

@ClusterScope
class ListSchemaViewModel @Inject constructor(
    val cluster: Cluster,
    val schemaRegistry: SchemaRegistry?,
    private val subjectComponentFactory: SubjectComponentFactory,
    private val tabViewModel: TabViewModel,
    private val windowsManager: WindowsManager
) : InsulatorViewModel() {

    private val schemasProperty: ObservableList<String> = FXCollections.observableArrayList()

    val selectedSchemaProperty = SimpleStringProperty()
    val searchItemProperty = SimpleStringProperty()

    val filteredSchemasProperty = SortedFilteredList(schemasProperty).apply {
        filterWhen(searchItemProperty) { p, i -> i.lowercase().contains(p.lowercase()) }
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

    fun refresh() = schemaRegistry
        ?.getAllSubjects()
        ?.map { it.sorted() }
        ?.map {
            it.runOnFXThread {
                schemasProperty.clear()
                schemasProperty.addAll(it)
            }
        }?.mapLeft {
            error.set(LoadSchemaListError(it.message ?: "Unable to load the schema list"))
        }

    suspend fun showSchema() {
        if (selectedSchemaProperty.value.isNullOrEmpty()) return
        schemaRegistry?.getSubject(selectedSchemaProperty.value!!)
            ?.map {
                subjectComponentFactory.build(it)
                    .getSchemaView()
                    .also { schemaViewTab -> schemaViewTab.whenUndockedOnce { refresh() } }
                    .let { schemaViewTab -> tabViewModel.setMainContent(it.name, schemaViewTab) }
            }
            ?.mapLeft { error.set(LoadSchemaError(it.message ?: "Unable to load the schema")) }
    }

    fun createNewSchema(owner: Window?) = windowsManager.openWindow("new-schema", owner) {
        subjectComponentFactory.build(Subject.empty())
            .getCreateSchemaView()
            .also { it.whenUndockedOnce { dispatch { refresh() } } }
    }
}

class LoadSchemaError(message: String) : Throwable(message)
class LoadSchemaListError(message: String) : Throwable(message)
