package insulator.views.main.topic

import insulator.viewmodel.TopicViewModel
import insulator.viewmodel.TopicsViewModel
import insulator.views.common.title
import insulator.views.configurations.AddClusterView
import javafx.geometry.Pos
import javafx.scene.layout.AnchorPane
import javafx.scene.paint.Color
import org.koin.core.KoinComponent
import org.koin.core.inject
import tornadofx.*

class ListTopicView : AnchorPane(), KoinComponent {
    private val viewModel: TopicsViewModel by inject()

    init {
        vbox {
            title("Topics", Color.ORANGERED)
            anchorpaneConstraints { topAnchor = 0;rightAnchor = 0;leftAnchor = 0 }
        }
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
            anchorpaneConstraints { topAnchor = 50;rightAnchor = 0;bottomAnchor = 0;leftAnchor = 0 }
        }
        paddingAll = 5.0
        anchorpaneConstraints { topAnchor = 0;rightAnchor = 0;bottomAnchor = 0;leftAnchor = 0 }
    }
}
