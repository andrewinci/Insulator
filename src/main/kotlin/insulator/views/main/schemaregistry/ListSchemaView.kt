package insulator.views.main.schemaregistry

import insulator.ui.common.InsulatorView
import insulator.ui.component.appBar
import insulator.ui.component.h1
import insulator.ui.component.searchBox
import insulator.ui.component.subTitle
import insulator.viewmodel.main.schemaregistry.ListSchemaViewModel
import insulator.viewmodel.main.schemaregistry.LoadSchemaListError
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
            subTitle(viewModel.subtitleProperty)
        }
        searchBox(viewModel.searchItemProperty, currentView = this@ListSchemaView)
        schemasListView()
    }

    private fun EventTarget.schemasListView() =
        listview<String> {
            cellFormat { graphic = label(it) }
            onDoubleClick { viewModel.showSchema() }
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
}
