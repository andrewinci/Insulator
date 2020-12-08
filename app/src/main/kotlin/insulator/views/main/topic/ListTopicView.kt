package insulator.views.main.topic

import insulator.di.ClusterScope
import insulator.helper.dispatch
import insulator.helper.hideOnReadonly
import insulator.ui.common.InsulatorView
import insulator.ui.component.action
import insulator.ui.component.appBar
import insulator.ui.component.refreshButton
import insulator.ui.component.searchBox
import insulator.viewmodel.main.topic.ListTopicViewModel
import insulator.views.configurations.ListClusterView
import javafx.event.EventTarget
import javafx.scene.control.SelectionMode
import javafx.scene.layout.Priority
import tornadofx.action
import tornadofx.bindSelected
import tornadofx.borderpane
import tornadofx.button
import tornadofx.label
import tornadofx.listview
import tornadofx.vbox
import tornadofx.vgrow
import javax.inject.Inject

@ClusterScope
class ListTopicView @Inject constructor(override val viewModel: ListTopicViewModel) : InsulatorView("Topics") {

    override val root = vbox(spacing = 5.0) {
        appBar {
            title = "Topics"
            subtitle = viewModel.subtitleProperty
            buttons = listOf(refreshButton("topic-list", viewModel::refresh))
        }
        borderpane {
            left = createTopicButton()
            right = searchBox(viewModel.searchItemProperty, this@ListTopicView, "search-box-list-topic")
        }
        topicsListView()
    }

    private fun EventTarget.topicsListView() =
        listview<String> {
            cellFormat { graphic = label(it) { id = "topic-$it" } }
            bindSelected(viewModel.selectedItemProperty)
            action { viewModel.dispatch { showTopic() } }
            itemsProperty().set(viewModel.filteredTopicsProperty)

            placeholder = label("No topic found")
            selectionModel.selectionMode = SelectionMode.SINGLE
            vgrow = Priority.ALWAYS
        }

    private fun EventTarget.createTopicButton() =
        button("Create topic") {
            id = "button-create-topic"
            action { viewModel.createNewTopic(currentWindow) }
        }.hideOnReadonly()

    override fun onError(throwable: Throwable) {
        replaceWith<ListClusterView>()
    }
}
