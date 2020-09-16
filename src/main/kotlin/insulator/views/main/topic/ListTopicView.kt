package insulator.views.main.topic

import insulator.viewmodel.main.topic.CreateTopicViewModel
import insulator.viewmodel.main.topic.ListTopicViewModel
import insulator.viewmodel.main.topic.TopicViewModel
import insulator.views.common.InsulatorView
import insulator.views.common.StringScope
import insulator.views.common.customOpenWindow
import insulator.views.common.searchBox
import insulator.views.configurations.ListClusterView
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.SelectionMode
import javafx.scene.layout.Priority
import javafx.stage.Modality
import javafx.stage.StageStyle
import tornadofx.* // ktlint-disable no-wildcard-imports

class ListTopicView : InsulatorView<ListTopicViewModel>("Topics", ListTopicViewModel::class) {

    private val searchItem = SimpleStringProperty()

    override val root = vbox(spacing = 5.0) {
        borderpane {
            left = hbox {
                button("Create topic") {
                    action {
                        with(StringScope("CreateNewTopic").withComponent(CreateTopicViewModel())) {
                            find<CreateTopicView>(this).also {
                                it.whenUndockedOnce { viewModel.refresh(); this.close() }
                            }.customOpenWindow(StageStyle.UTILITY, Modality.WINDOW_MODAL)
                        }
                    }
                }
            }
            right = searchBox(searchItem)
        }
        listview<String> {
            cellFormat { graphic = label(it) }
            onDoubleClick {
                if (this.selectedItem == null) return@onDoubleClick
                with(StringScope(this.selectedItem!!).withComponent(TopicViewModel(this.selectedItem!!))) {
                    find<TopicView>(this).also { it.whenUndockedOnce { viewModel.refresh() } }.customOpenWindow()
                }
            }
            itemsProperty().set(
                SortedFilteredList(viewModel.topicList).apply {
                    filterWhen(searchItem) { p, i -> i.toLowerCase().contains(p.toLowerCase()) }
                }.filteredItems
            )
            placeholder = label("No topic found")
            selectionModel.selectionMode = SelectionMode.SINGLE
            vgrow = Priority.ALWAYS
        }
    }

    override fun onError(throwable: Throwable) {
        replaceWith<ListClusterView>()
    }
}
