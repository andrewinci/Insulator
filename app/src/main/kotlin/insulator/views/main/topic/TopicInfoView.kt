package insulator.views.main.topic

import insulator.kafka.model.Topic
import insulator.ui.common.InsulatorView
import insulator.ui.component.appBar
import insulator.ui.component.fieldName
import insulator.viewmodel.common.InsulatorViewModel
import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import tornadofx.* // ktlint-disable no-wildcard-imports

class TopicInfoViewModel(val topic: Topic) : InsulatorViewModel()

class TopicInfoView(
    override val viewModel: TopicInfoViewModel
) : InsulatorView() {

    val topic = viewModel.topic

    override val root = vbox(spacing = 10.0) {
        appBar { title = "Info ${topic.name}" }
        field("Name", topic.name)
        field("Is Internal", topic.isInternal.toString())
        field("Partition Count", topic.partitionCount.toString())
        field("Messages Count", topic.messageCount.toString())
        field("Replication Factor", topic.replicationFactor.toString())
        field("Is Compacted", topic.isCompacted.toString())

        tableview<Pair<String, String>> {
            readonlyColumn("Configuration Name", Pair<String, String>::first) {
                prefWidthProperty().bind(this.tableView.widthProperty().divide(2).minus(10))
                isReorderable = false
            }
            readonlyColumn("Value", Pair<String, String>::second) {
                prefWidthProperty().bind(this.tableView.widthProperty().divide(2).minus(10))
                isReorderable = false
            }
            items = observableListOf(topic.configuration.rawConfiguration.toList())
            hgrow = Priority.ALWAYS
            vgrow = Priority.ALWAYS
        }

        borderpane {
            right = button("Close") {
                action { close() }
            }
        }

        prefWidth = 800.0
        prefHeight = 800.0
    }

    private fun EventTarget.field(title: String, value: String) = with(this) {
        hbox(spacing = 10.0, alignment = Pos.CENTER_LEFT) {
            fieldName(title) { minWidth = 120.0; alignment = Pos.CENTER }
            textfield(value) {
                hgrow = Priority.ALWAYS
                isEditable = false
            }
        }
    }
}
