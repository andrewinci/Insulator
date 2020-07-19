package insulator.views.main.topic

import insulator.Styles
import insulator.lib.kafka.ConsumeFrom
import insulator.lib.kafka.DeserializationFormat
import insulator.viewmodel.main.topic.RecordViewModel
import insulator.viewmodel.main.topic.TopicViewModel
import insulator.views.common.keyValueLabel
import javafx.collections.FXCollections
import javafx.geometry.Pos
import javafx.scene.control.TabPane
import tornadofx.*


class TopicView : View() {

    private val viewModel: TopicViewModel by inject()

    override val root = vbox {
        label(viewModel.nameProperty.value) { addClass(Styles.h1, Styles.mainColor) }
        keyValueLabel("Approximate message count", viewModel.messageCountProperty)
        keyValueLabel("Internal topic", viewModel.internalProperty)
        keyValueLabel("Partitions count", viewModel.partitionsProperty)

        tabpane {
            tab("Consumer") {
                vbox {
                    hbox {
                        label("key")
                        combobox<String> {
                            items = FXCollections.observableArrayList(DeserializationFormat.values().map { it.name }.toList())
                            valueProperty().bindBidirectional(viewModel.deserializeKeyProperty)
                        }
                        label("value")
                        combobox<String> {
                            items = FXCollections.observableArrayList(DeserializationFormat.values().map { it.name }.toList())
                            valueProperty().bindBidirectional(viewModel.deserializeValueProperty)
                        }
                        label("from")
                        combobox<String> {
                            items = FXCollections.observableArrayList(ConsumeFrom.values().map { it.name }.toList())
                            valueProperty().bindBidirectional(viewModel.consumeFromProperty)
                        }
                        button(viewModel.consumeButtonText) {
                            action { viewModel.consumeButtonClick() }
                            prefWidth = 80.0
                        }
                        button("Clear") { action { viewModel.clear() } }
                        spacing = 5.0
                        paddingAll = 5.0
                        alignment = Pos.CENTER_RIGHT
                    }
                    tableview(viewModel.records) {
                        column("Time", RecordViewModel::timestampProperty).minWidth(200.0)
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
        addClass(Styles.card)
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