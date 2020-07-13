package insulator.views.main.topic

import insulator.model.Topic
import insulator.views.common.SizedView
import insulator.views.common.card
import insulator.views.common.title
import insulator.views.main.OverviewView
import javafx.scene.paint.Color
import tornadofx.*


class TopicView(topicName: String) : SizedView(topicName, 800.0, 800.0) {

    override val root = card(topicName) {
    }

}