package insulator.views.main.topic

import insulator.Styles
import insulator.di.GlobalConfiguration
import insulator.lib.kafka.ConsumeFrom
import insulator.lib.kafka.DeserializationFormat
import insulator.viewmodel.main.topic.RecordViewModel
import insulator.viewmodel.main.topic.TopicViewModel
import insulator.views.common.keyValueLabel
import insulator.views.common.searchBox
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.SelectionMode
import javafx.scene.control.TabPane
import javafx.scene.control.TableView
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent
import javafx.scene.layout.Priority
import tornadofx.*


class TopicView : View() {

    private val viewModel: TopicViewModel by inject()
    private val searchItem = SimpleStringProperty()
    private val subtitleProperty by lazy {
        val message = { count: Long -> "Message count: $count" }
        val res = SimpleStringProperty(message(viewModel.messageCountProperty.value))
        viewModel.messageCountProperty.onChange { res.value = message(it) }
        res
    }

    override val root = borderpane {
        top = vbox {
            vbox {
                label(viewModel.nameProperty.value) { addClass(Styles.h1) }
                label(subtitleProperty) { addClass(Styles.h3) }
                addClass(Styles.topBarMenu, Styles.subtitle)
            }
            hbox { addClass(Styles.topBarMenuShadow) }
//            keyValueLabel("Internal topic", viewModel.internalProperty)
//            keyValueLabel("Partitions count", viewModel.partitionsProperty)
        }
        center = vbox(spacing = 2.0) {
            borderpane {
                left = button(viewModel.consumeButtonText) { action { viewModel.consumeButtonClick() }; prefWidth = 80.0 }
                center = hbox(alignment = Pos.CENTER){
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
                selectionModel.selectionMode = SelectionMode.SINGLE
                onDoubleClick {
                    //todo: improve UX
                    if (selectedItem !is RecordViewModel) return@onDoubleClick
                    val content = Clipboard.getSystemClipboard()
                    content.putString("${selectedItem!!.timestampProperty.value}\t" +
                            "${selectedItem!!.keyProperty.value}\t" +
                            selectedItem!!.valueProperty.value)
                }
                vgrow = Priority.ALWAYS
            }
        }
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