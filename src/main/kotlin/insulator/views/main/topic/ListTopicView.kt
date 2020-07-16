package insulator.views.main.topic

import insulator.viewmodel.TopicViewModel
import insulator.viewmodel.ListTopicViewModel
import insulator.views.common.card
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.transformation.FilteredList
import javafx.collections.transformation.SortedList
import javafx.geometry.Pos
import javafx.scene.control.SelectionMode
import tornadofx.*

class ListTopicView : View() {

    private val viewModel: ListTopicViewModel by inject()
    private val searchItem = SimpleStringProperty()

    override val root = card("Topics") {
        hbox { label("Search"); textfield(searchItem) { minWidth = 200.0 }; alignment = Pos.CENTER_RIGHT; spacing = 5.0 }
        listview<TopicViewModel> {
            cellFormat {
                graphic = label(it.nameProperty)
            }
            onDoubleClick {
                if (this.selectedItem == null) return@onDoubleClick
                val scope = Scope()
                tornadofx.setInScope(this.selectedItem!!, scope)
                find<TopicView>(scope).openWindow()
            }
            runAsync {
                itemsProperty().set(
                        SortedFilteredList(viewModel.listTopics()).apply {
                            filterWhen(searchItem) { p, i -> i.nameProperty.value.contains(p) }
                        }.filteredItems
                )
            }

            selectionModel.selectionMode = SelectionMode.SINGLE
            prefHeight = 600.0 //todo: remove hardcoded and retrieve
        }
        spacing = 5.0
        paddingAll = 5.0
    }

}