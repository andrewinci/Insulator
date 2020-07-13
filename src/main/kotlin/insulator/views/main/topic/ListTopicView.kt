package insulator.views.main.topic

import insulator.viewmodel.TopicViewModel
import insulator.viewmodel.ListTopicViewModel
import insulator.views.common.card
import javafx.scene.layout.VBox
import org.koin.core.KoinComponent
import org.koin.core.inject
import tornadofx.*

class ListTopicView : VBox(), KoinComponent {
    private val viewModel: ListTopicViewModel by inject()

    init {
        card("Topics") {
            tableview(viewModel.topicsProperty) {
                column("", TopicViewModel::nameProperty).fixedWidth(25.0).cellFormat {
                    graphic = button("i") {
                        action { TopicView(getTopicViewModel(it)).openWindow() }
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

    private fun getTopicViewModel(name: String) = viewModel.topicsProperty.filter { it.nameProperty.value == name }.first()
}