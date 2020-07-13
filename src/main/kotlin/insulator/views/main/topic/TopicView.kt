package insulator.views.main.topic

import insulator.model.Topic
import insulator.views.common.title
import insulator.views.main.OverviewView
import javafx.scene.paint.Color
import tornadofx.*


class TopicView(topicName: String) : View(topicName) {

    override val root = hbox {
        title(topicName, Color.ORANGERED)
        paddingAll = 5.0
    }

    override fun onDock() {
        super.onDock()
        super.currentStage?.width = 800.0
        super.currentStage?.height = 800.0
    }

}