package insulator.views.main.topic

import insulator.viewmodel.RecordViewModel
import insulator.viewmodel.TopicViewModel
import insulator.views.common.card
import insulator.views.common.keyValueLabel
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleLongProperty
import javafx.scene.control.TabPane
import tornadofx.*


class TopicView : View() {

    private val viewModel: TopicViewModel by inject()

    override val root = card(viewModel.nameProperty.value) {
        keyValueLabel("Approximate message count", viewModel.messageCountProperty)
        keyValueLabel("Internal topic", viewModel.internalProperty)
        keyValueLabel("Partitions count", viewModel.partitionsProperty)

        tabpane {
            tab("Consumer") {
                vbox {
                    buttonbar() {
                        button(viewModel.consumeButtonText) { action { viewModel.consumeButtonClick() } }
                        button("Clear") { action { viewModel.clear() } }
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

    override fun onDock() {
        currentWindow?.setOnCloseRequest {
            viewModel.stop()
        }
        super.currentStage?.width = 800.0
        super.currentStage?.height = 800.0
        viewModel.loadDetails()
        super.onDock()
    }
}