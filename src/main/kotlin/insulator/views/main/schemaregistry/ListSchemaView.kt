package insulator.views.main.schemaregistry

import insulator.viewmodel.main.schemaregistry.ListSchemaViewModel
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.SelectionMode
import javafx.scene.layout.Priority
import tornadofx.*

class ListSchemaView : View("Schema registry") {

    private val viewModel: ListSchemaViewModel by inject()
    private val searchItem = SimpleStringProperty()

    override val root = vbox(spacing = 5.0) {
        hbox { label("Search"); textfield(searchItem) { minWidth = 200.0 }; alignment = Pos.CENTER_RIGHT; spacing = 5.0 }
        listview<String> {
            cellFormat {
                graphic = label(it)
            }
            onDoubleClick {
                if (this.selectedItem == null) return@onDoubleClick
                val scope = Scope()
                tornadofx.setInScope(viewModel.getSchema(this.selectedItem!!), scope)
                find<SchemaView>(scope).openWindow()
            }
            runAsync {
                itemsProperty().set(
                        SortedFilteredList(viewModel.listSchemas()).apply {
                            filterWhen(searchItem) { p, i -> i.toLowerCase().contains(p.toLowerCase()) }
                        }.filteredItems
                )
            }

            selectionModel.selectionMode = SelectionMode.SINGLE
            vgrow = Priority.ALWAYS
        }
    }
}