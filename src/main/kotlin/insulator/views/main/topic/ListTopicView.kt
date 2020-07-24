package insulator.views.main.topic

import insulator.Styles
import insulator.viewmodel.main.topic.TopicViewModel
import insulator.viewmodel.main.topic.ListTopicViewModel
import insulator.views.common.searchBox
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.SelectionMode
import javafx.scene.layout.Priority
import tornadofx.*

class ListTopicView : View("Topics") {

    private val viewModel: ListTopicViewModel by inject()
    private val searchItem = SimpleStringProperty()

    override val root = vbox(spacing = 5.0) {
        searchBox(searchItem)
        listview<TopicViewModel> {
            cellFormat { graphic = label(it.nameProperty) }
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
            vgrow = Priority.ALWAYS
        }
    }
}