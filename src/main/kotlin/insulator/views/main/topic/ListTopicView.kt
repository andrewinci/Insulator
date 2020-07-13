package insulator.views.main.topic

import insulator.viewmodel.TopicViewModel
import insulator.viewmodel.TopicsViewModel
import insulator.views.common.card
import insulator.views.common.title
import insulator.views.configurations.AddClusterView
import javafx.geometry.Pos
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import org.koin.core.KoinComponent
import org.koin.core.inject
import tornadofx.*

class ListTopicView : VBox(), KoinComponent {
    private val viewModel: TopicsViewModel by inject()

    init {
        card("Topics") {
            tableview(viewModel.topicsProperty) {
                column("", TopicViewModel::nameProperty).fixedWidth(25.0).cellFormat {
                    graphic = button("i") {
                        action { TopicView(it).openWindow() }
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
}