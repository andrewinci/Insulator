package insulator.views.main.topic

import insulator.viewmodel.TopicViewModel
import insulator.viewmodel.ListTopicViewModel
import insulator.views.common.card
import tornadofx.*

class ListTopicView : View() {

    private val viewModel: ListTopicViewModel by inject()

    override val root = vbox {
        card("Topics") {
            tableview(viewModel.topicsProperty) {
                column("", TopicViewModel::nameProperty).fixedWidth(25.0).cellFormat {
                    graphic = button("i") {
                        action {
                            val scope = Scope()
                            tornadofx.setInScope(getTopicViewModel(it), scope)
                            find<TopicView>(scope).openWindow()
                        }
                    }
                }
                column("Topic", TopicViewModel::nameProperty).minWidth(200.0)
                column("#Messages", TopicViewModel::messageCountProperty)
                column("Internal", TopicViewModel::internalProperty)
                column("#Partitions", TopicViewModel::partitionsProperty)
                prefHeight = 600.0 //todo: remove hardcoded and retrieve
            }
        }
    }

    private fun getTopicViewModel(name: String) = viewModel.topicsProperty.first { it.nameProperty.value == name }
}