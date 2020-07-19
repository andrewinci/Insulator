package insulator.views.main.topic

import insulator.Styles
import insulator.di.GlobalConfiguration
import insulator.lib.kafka.ConsumeFrom
import insulator.lib.kafka.DeserializationFormat
import insulator.viewmodel.main.topic.RecordViewModel
import insulator.viewmodel.main.topic.TopicViewModel
import insulator.views.common.keyValueLabel
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.geometry.Pos
import javafx.scene.control.TabPane
import tornadofx.*


class TopicView : View() {

    private val viewModel: TopicViewModel by inject()
    private val searchItem = SimpleStringProperty()

    override val root = vbox {
        label(viewModel.nameProperty.value) { addClass(Styles.h1, Styles.mainColor) }
        keyValueLabel("Approximate message count", viewModel.messageCountProperty)
        keyValueLabel("Internal topic", viewModel.internalProperty)
        keyValueLabel("Partitions count", viewModel.partitionsProperty)

        tabpane {
            tab("Consumer") {
                vbox {
                    borderpane {
                        right = hbox {
                            if (GlobalConfiguration.currentCluster.isSchemaRegistryConfigured()) {
                                label("value format")
                                combobox<String> {
                                    items = FXCollections.observableArrayList(DeserializationFormat.values().map { it.name }.toList())
                                    valueProperty().bindBidirectional(viewModel.deserializeValueProperty)
                                }
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

                            alignment = Pos.CENTER_RIGHT
                        }
                        left = hbox { label("Search"); textfield(searchItem) { minWidth = 200.0 }; alignment = Pos.CENTER_LEFT; spacing = 5.0 }
                        paddingAll = 5.0
                    }

                    tableview<RecordViewModel> {
                        column("Time", RecordViewModel::timestampProperty).minWidth(200.0)
                        column("Key", RecordViewModel::keyProperty).minWidth(200.0)
                        column("Value", RecordViewModel::valueProperty).minWidth(200.0)
                        prefHeight = 600.0 //todo: remove hardcoded and retrieve
                        itemsProperty().set(
                                SortedFilteredList(viewModel.records).apply {
                                    filterWhen(searchItem) { p, i -> i.keyProperty.value.contains(p) || i.valueProperty.value.contains(p)}
                                }.filteredItems
                        )
                    }
                }
                spacing = 5.0
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