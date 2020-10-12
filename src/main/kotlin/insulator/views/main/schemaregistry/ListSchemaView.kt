package insulator.views.main.schemaregistry

import insulator.viewmodel.main.schemaregistry.ListSchemaViewModel
import insulator.viewmodel.main.schemaregistry.LoadSchemaListError
import insulator.views.common.InsulatorView
import insulator.views.component.appBar
import insulator.views.component.h1
import insulator.views.component.searchBox
import javafx.event.EventTarget
import javafx.scene.control.SelectionMode
import javafx.scene.layout.Priority
import tornadofx.* // ktlint-disable no-wildcard-imports

class ListSchemaView : InsulatorView<ListSchemaViewModel>("Schema registry", ListSchemaViewModel::class) {

    override val root = vbox(spacing = 5.0) {
        appBar {
            hbox {
                h1("Schema registry")
            }
        }
        searchBox(viewModel.searchItem, currentView = this@ListSchemaView)
        schemasListView()
    }

    private fun EventTarget.schemasListView() =
        listview<String> {
            cellFormat { graphic = label(it) }
            onDoubleClick { viewModel.showSchema() }
            itemsProperty().set(viewModel.filteredSchemas)
            bindSelected(viewModel.selectedSchema)

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
}
