package insulator.views.main.topic

import insulator.lib.kafka.ConsumeFrom
import insulator.lib.kafka.DeserializationFormat
import insulator.styles.Controls
import insulator.ui.component.appBar
import insulator.ui.component.blueButton
import insulator.ui.component.confirmationButton
import insulator.ui.component.h1
import insulator.ui.component.subTitle
import insulator.viewmodel.main.topic.ProducerViewModel
import insulator.viewmodel.main.topic.RecordViewModel
import insulator.viewmodel.main.topic.TopicViewModel
import insulator.views.common.InsulatorView
import insulator.views.common.StringScope
import insulator.views.common.customOpenWindow
import insulator.views.common.searchBox
import javafx.beans.binding.Bindings
import javafx.collections.FXCollections
import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.control.SelectionMode
import javafx.scene.layout.Priority
import tornadofx.* // ktlint-disable no-wildcard-imports

class TopicView : InsulatorView<TopicViewModel>(viewModelClazz = TopicViewModel::class) {

    override val root = borderpane {
        top = appBar {
            hbox(spacing = 10.0, alignment = Pos.CENTER_LEFT) {
                h1(viewModel.nameProperty.value)
                confirmationButton("delete", "The topic \"${viewModel.nameProperty.value}\" will be removed.") {
                    viewModel.delete()
                    close()
                }
            }
            subTitle(viewModel.subtitleProperty)
        }
        center = vbox(spacing = 2.0) {
            borderpane {
                left = hbox(alignment = Pos.CENTER, spacing = 5.0) {
                    blueButton("Produce") {
                        with(StringScope(viewModel.topicName).withComponent(ProducerViewModel(viewModel.topicName))) {
                            find<ProducerView>(this).customOpenWindow(owner = null)
                        }
                    }

                    button(viewModel.consumeButtonText) { action { viewModel.consume() } }
                    label("from")
                    combobox<String> {
                        items = FXCollections.observableArrayList(ConsumeFrom.values().map { it.name }.toList())
                        valueProperty().bindBidirectional(viewModel.consumeFromProperty)
                    }
                    if (viewModel.cluster.isSchemaRegistryConfigured()) {
                        viewModel.deserializeValueProperty.set(DeserializationFormat.Avro.name)
                        label("value format")
                        combobox<String> {
                            items = FXCollections.observableArrayList(DeserializationFormat.values().map { it.name }.toList())
                            valueProperty().bindBidirectional(viewModel.deserializeValueProperty)
                        }
                    }
                    region { minWidth = 10.0 }
                    button("Clear") { action { viewModel.clear() } }
                }
                right = searchBox(viewModel.searchItem)
            }
            recordsTable()
        }
        addClass(Controls.view)
    }

    private fun EventTarget.recordsTable() = apply {
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
            viewModel.filteredRecords.set(
                SortedFilteredList(viewModel.records).apply {
                    filterWhen(viewModel.searchItem) { p, i ->
                        i.keyProperty.value?.toLowerCase()?.contains(p.toLowerCase()) ?: false ||
                            i.valueProperty.value.toLowerCase().contains(p.toLowerCase())
                    }
                }.sortedItems.also {
                    it.comparatorProperty().bind(this.comparatorProperty())
                }
            )
            itemsProperty().bind(viewModel.filteredRecords)
            contextMenu = contextmenu {
                item("Copy") { action { viewModel.copySelectedRecordToClipboard() } }
                item("Copy all") { action { viewModel.copyAllRecordsToClipboard() } }
            }

            bindSelected(viewModel.selectedItem)
            selectionModel.selectionMode = SelectionMode.SINGLE
            vgrow = Priority.ALWAYS
            prefWidth = 800.0
            prefHeight = 800.0
        }
    }

    override fun onDock() {
        currentWindow?.setOnCloseRequest { viewModel.stop() }
        titleProperty.bind(Bindings.createStringBinding({ "${viewModel.nameProperty.value} ${viewModel.cluster.name}" }, viewModel.nameProperty))
        super.onDock()
    }

    override fun onError(throwable: Throwable) {
        close()
    }
}
