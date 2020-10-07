package insulator.views.main.topic

import insulator.ui.component.action
import insulator.viewmodel.main.topic.ListTopicViewModel
import insulator.views.common.InsulatorView
import insulator.views.common.searchBox
import insulator.views.configurations.ListClusterView
import javafx.event.EventTarget
import javafx.scene.control.SelectionMode
import javafx.scene.layout.Priority
import tornadofx.* // ktlint-disable no-wildcard-imports

class ListTopicView : InsulatorView<ListTopicViewModel>("Topics", ListTopicViewModel::class) {

    override val root = vbox(spacing = 5.0) {
        borderpane {
            left = createTopicButton()
            right = searchBox(viewModel.searchItem, this@ListTopicView)
        }
        topicsListView()
    }

    private fun EventTarget.topicsListView() =
        listview<String> {
            cellFormat { graphic = label(it) { id = "topic-$it" } }
            bindSelected(viewModel.selectedItem)
            action { viewModel.showTopic() }
            itemsProperty().set(viewModel.filteredTopics)

            placeholder = label("No topic found")
            selectionModel.selectionMode = SelectionMode.SINGLE
            vgrow = Priority.ALWAYS
        }

    private fun EventTarget.createTopicButton() =
        button("Create topic") {
            action { viewModel.createNewTopic() }
        }

    override fun onError(throwable: Throwable) {
        replaceWith<ListClusterView>()
    }
}
