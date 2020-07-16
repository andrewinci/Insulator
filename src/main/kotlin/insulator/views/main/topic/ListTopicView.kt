package insulator.views.main.topic

import insulator.viewmodel.TopicViewModel
import insulator.viewmodel.ListTopicViewModel
import insulator.views.common.card
import javafx.scene.control.SelectionMode
import tornadofx.*

class ListTopicView : View() {

    private val viewModel: ListTopicViewModel by inject()

    override val root = vbox {
        card("Topics") {
            tableview<TopicViewModel> {
                column("Topic", TopicViewModel::nameProperty).minWidth(200.0)
                column("Internal", TopicViewModel::internalProperty)
                column("#Partitions", TopicViewModel::partitionsProperty)

                onDoubleClick {
                    if (this.selectedItem == null) return@onDoubleClick
                    val scope = Scope()
                    tornadofx.setInScope(this.selectedItem!!, scope)
                    find<TopicView>(scope).openWindow()
                }

                asyncItems { viewModel.listTopics() }

                selectionModel.selectionMode = SelectionMode.SINGLE
                prefHeight = 600.0 //todo: remove hardcoded and retrieve
            }
        }
    }
}