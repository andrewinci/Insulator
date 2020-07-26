package insulator.views.main.topic

import insulator.Styles
import insulator.di.GlobalConfiguration
import insulator.lib.kafka.ConsumeFrom
import insulator.lib.kafka.DeserializationFormat
import insulator.viewmodel.main.topic.RecordViewModel
import insulator.viewmodel.main.topic.TopicViewModel
import insulator.views.common.searchBox
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.geometry.Pos
import javafx.scene.control.SelectionMode
import javafx.scene.control.TableView
import javafx.scene.input.Clipboard
import javafx.scene.layout.Priority
import tornadofx.*
import java.util.concurrent.Callable


class TopicView : View() {

    private val viewModel: TopicViewModel by inject()
    private val searchItem = SimpleStringProperty()
    private val subtitleProperty = SimpleStringProperty().also {
        it.bind(Bindings.createStringBinding(Callable {
            "Message count: ${viewModel.messageCountProperty.value} - " +
                    "Is internal: ${viewModel.isInternalProperty.value} - " +
                    "Partitions count: ${viewModel.partitionCountProperty.value}"
        }, viewModel.messageCountProperty))
    }

    override val root = borderpane {
        top = vbox {
            vbox {
                label(viewModel.nameProperty.value) { addClass(Styles.h1) }
                label(subtitleProperty) { addClass(Styles.h3) }
                addClass(Styles.topBarMenu, Styles.subtitle)
            }
            hbox { addClass(Styles.topBarMenuShadow) }
        }
        center = vbox(spacing = 2.0) {
            borderpane {
                left = button(viewModel.consumeButtonText) { action { viewModel.consumeButtonClick() }; prefWidth = 80.0 }
                center = hbox(alignment = Pos.CENTER) {
                    label("from")
                    combobox<String> {
                        items = FXCollections.observableArrayList(ConsumeFrom.values().map { it.name }.toList())
                        valueProperty().bindBidirectional(viewModel.consumeFromProperty)
                    }
                    if (GlobalConfiguration.currentCluster.isSchemaRegistryConfigured()) {
                        label("value format")
                        combobox<String> {
                            items = FXCollections.observableArrayList(DeserializationFormat.values().map { it.name }.toList())
                            valueProperty().bindBidirectional(viewModel.deserializeValueProperty)
                        }
                    }
                    region { minWidth = 10.0 }
                    button("Clear") { action { viewModel.clear() } }
                }
                right = searchBox(searchItem)
            }
            tableview<RecordViewModel> {
                column("Time", RecordViewModel::timestampProperty) {
                    prefWidthProperty().bind(this.tableView.widthProperty().divide(4).multiply(1))
                }
                column("Key", RecordViewModel::keyProperty) {
                    prefWidthProperty().bind(this.tableView.widthProperty().divide(4).multiply(1))
                }
                column("Value", RecordViewModel::valueProperty) {
                    prefWidthProperty().bind(this.tableView.widthProperty().divide(4).multiply(2).minus(4.0))
                    enableTextWrap()
                }
                itemsProperty().set(recordList())
                contextMenu = contextmenu {
                    item("Copy") {
                        action {
                            if (selectedItem !is RecordViewModel) return@action
                            Clipboard.getSystemClipboard().putString(selectedItem!!.toCsv())
                        }
                    }
                    item("Copy all") {
                        action {
                            Clipboard.getSystemClipboard().putString(recordList().joinToString("\n") { it.toCsv() })
                        }
                    }
                }
                selectionModel.selectionMode = SelectionMode.SINGLE
                vgrow = Priority.ALWAYS
            }
        }
        addClass(Styles.view)
    }

    private fun TableView<RecordViewModel>.recordList() =
            SortedFilteredList(viewModel.records).apply {
                filterWhen(searchItem) { p, i ->
                    i.keyProperty.value?.toLowerCase()?.contains(p.toLowerCase()) ?: false ||
                            i.valueProperty.value.toLowerCase().contains(p.toLowerCase())
                }
            }.sortedItems.also {
                it.comparatorProperty().bind(this.comparatorProperty())
            }


    private fun RecordViewModel.toCsv() = "${this.timestampProperty.value}\t" +
            "${this.keyProperty.value}\t" +
            this.valueProperty.value


    override fun onDock() {
        currentWindow?.setOnCloseRequest { viewModel.stop() }
        super.currentStage?.width = 800.0
        super.currentStage?.height = 800.0
        super.onDock()
    }
}

