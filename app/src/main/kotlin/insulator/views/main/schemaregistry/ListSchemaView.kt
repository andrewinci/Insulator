package insulator.views.main.schemaregistry

import insulator.di.ClusterScope
import insulator.helper.dispatch
import insulator.ui.common.InsulatorView
import insulator.ui.component.appBar
import insulator.ui.component.refreshButton
import insulator.ui.component.searchBox
import insulator.viewmodel.main.schemaregistry.ListSchemaViewModel
import insulator.viewmodel.main.schemaregistry.LoadSchemaListError
import javafx.event.EventTarget
import javafx.scene.control.SelectionMode
import javafx.scene.layout.Priority
import tornadofx.action
import tornadofx.bindSelected
import tornadofx.borderpane
import tornadofx.button
import tornadofx.label
import tornadofx.listview
import tornadofx.onDoubleClick
import tornadofx.vbox
import tornadofx.vgrow
import javax.inject.Inject

@ClusterScope
class ListSchemaView @Inject constructor(
    override val viewModel: ListSchemaViewModel
) : InsulatorView("Schema registry") {

    override val root = vbox(spacing = 5.0) {
        appBar {
            title = "Schema registry"
            subtitle = viewModel.subtitleProperty
            buttons = listOf(refreshButton("schema-list", viewModel::refresh))
        }
        borderpane {
            left = createSchemaButton()
            right = searchBox(viewModel.searchItemProperty, currentView = this@ListSchemaView)
        }
        schemasListView()
    }

    private fun EventTarget.schemasListView() =
        listview<String> {
            cellFormat { graphic = label(it) { id = "schema-$it" } }
            onDoubleClick { viewModel.dispatch { showSchema() } }
            itemsProperty().set(viewModel.filteredSchemasProperty)
            bindSelected(viewModel.selectedSchemaProperty)

            placeholder = label("No schema found")
            selectionModel.selectionMode = SelectionMode.SINGLE
            vgrow = Priority.ALWAYS
        }

    override fun onError(throwable: Throwable) {
        when (throwable) {
            is LoadSchemaListError -> return
            else -> viewModel.refresh()
        }
    }

    private fun EventTarget.createSchemaButton() =
        button("Create schema") {
            action { viewModel.createNewSchema(currentWindow) }
            id = "button-create-schema"
        }
}
