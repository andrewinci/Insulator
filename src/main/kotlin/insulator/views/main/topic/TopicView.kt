package insulator.views.main.topic

import insulator.lib.kafka.ConsumeFrom
import insulator.viewmodel.RecordViewModel
import insulator.viewmodel.TopicViewModel
import insulator.views.common.SizedView
import insulator.views.common.card
import javafx.scene.control.Button
import javafx.scene.control.ButtonBar
import javafx.scene.control.TabPane
import tornadofx.*


class TopicView(private val viewModel: TopicViewModel) : SizedView(viewModel.nameProperty.value, 800.0, 800.0) {

    override val root = card(viewModel.nameProperty.value) {
        tabpane {
            tab("Consumer") {
                vbox {
                    buttonbar() {
                        consumeButton()
                        button("Clean"){action { viewModel.clean() }}
                    }
                    tableview(viewModel.records) {
                        column("Time", RecordViewModel::timestamp).minWidth(200.0)
                        column("Key", RecordViewModel::keyProperty).minWidth(200.0)
                        column("Value", RecordViewModel::valueProperty).minWidth(200.0)
                        prefHeight = 600.0 //todo: remove hardcoded and retrieve
                    }
                }
                spacing = 5.0
            }
            tab("Producer") {

            }
            tab("Settings") {

            }
            tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
        }
    }

    private fun ButtonBar.consumeButton() = Button("Consume").also {
        it.action {
            if (it.text == "Consume") {
                it.text = "Stop"
                viewModel.clean()
                viewModel.consume(from = ConsumeFrom.Beginning)
            } else {
                it.text = "Consume"
                viewModel.stopConsumer()
            }
        }
    }.also { this.buttons.add(it) }

    override fun onDock() {
        currentWindow?.setOnCloseRequest {
            viewModel.stopConsumer()
        }
        super.onDock()
    }
}