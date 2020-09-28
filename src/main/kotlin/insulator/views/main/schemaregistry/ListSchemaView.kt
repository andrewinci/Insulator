package insulator.views.main.schemaregistry

import insulator.viewmodel.main.schemaregistry.ListSchemaViewModel
import insulator.viewmodel.main.schemaregistry.LoadSchemaListError
import insulator.views.common.InsulatorView
import insulator.views.common.searchBox
import insulator.views.configurations.ListClusterView
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.SelectionMode
import javafx.scene.layout.Priority
import tornadofx.* // ktlint-disable no-wildcard-imports

class ListSchemaView : InsulatorView<ListSchemaViewModel>("Schema registry", ListSchemaViewModel::class) {

    private val searchItem = SimpleStringProperty()

    override val root = vbox(spacing = 5.0) {
        searchBox(searchItem).also { shortcut("CTRL+F") { it.requestFocus() } }
        listview<String> {
            cellFormat { graphic = label(it) }
            onDoubleClick { viewModel.showSchema() }
            itemsProperty().set(
                SortedFilteredList(viewModel.listSchema).apply {
                    filterWhen(searchItem) { p, i -> i.toLowerCase().contains(p.toLowerCase()) }
                }.filteredItems
            )

            bindSelected(viewModel.selectedSchema)
            placeholder = label("No schema found")
            selectionModel.selectionMode = SelectionMode.SINGLE
            vgrow = Priority.ALWAYS
        }
    }

    override fun onError(throwable: Throwable) {
        when (throwable) {
            is LoadSchemaListError -> replaceWith<ListClusterView>()
            else -> viewModel.refresh()
        }
    }
}
