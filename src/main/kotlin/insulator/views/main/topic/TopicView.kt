package insulator.views.main.topic

import insulator.lib.configuration.model.Cluster
import insulator.lib.helpers.dispatch
import insulator.lib.kafka.ConsumeFrom
import insulator.lib.kafka.DeserializationFormat
import insulator.ui.common.InsulatorTabView
import insulator.ui.component.appBar
import insulator.ui.component.blueButton
import insulator.ui.component.confirmationButton
import insulator.ui.component.fieldName
import insulator.ui.component.h1
import insulator.ui.component.searchBox
import insulator.ui.component.subTitle
import insulator.viewmodel.main.topic.RecordViewModel
import insulator.viewmodel.main.topic.TopicViewModel
import javafx.beans.binding.Bindings
import javafx.collections.FXCollections
import javafx.event.EventTarget
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Control
import javafx.scene.control.SelectionMode
import javafx.scene.control.TableCell
import javafx.scene.layout.Priority
import tornadofx.* // ktlint-disable no-wildcard-imports

class TopicView : InsulatorTabView<TopicViewModel>(viewModelClazz = TopicViewModel::class) {

    private val cluster: Cluster by di()

    override val root = vbox {
        appBar {
            hbox(alignment = Pos.CENTER_LEFT, spacing = 5.0) {
                h1(viewModel.nameProperty.value)
                deleteButton()
            }
            subTitle(viewModel.subtitleProperty)
        }
        borderpane {
            padding = Insets(-5.0, 0.0, 10.0, 0.0)
            left = hbox(alignment = Pos.CENTER, spacing = 5.0) {
                blueButton("Produce") { viewModel.showProduceView() }
                button(viewModel.consumeButtonText) { action { viewModel.dispatch { consume() } } }
                fieldName("from")
                consumeFromCombobox()
                valueFormatOptions()
                button("Clear") { action { viewModel.clear() } }
            }
            right = searchBox(viewModel.searchItem, this@TopicView)
        }
        recordsTable()
    }

    private fun EventTarget.valueFormatOptions() {
        if (cluster.isSchemaRegistryConfigured()) {
            viewModel.deserializeValueProperty.set(DeserializationFormat.Avro.name)
            fieldName("value format")
            combobox<String> {
                items = FXCollections.observableArrayList(DeserializationFormat.values().map { it.name }.toList())
                valueProperty().bindBidirectional(viewModel.deserializeValueProperty)
            }
        }
    }

    private fun EventTarget.consumeFromCombobox() =
        combobox<String> {
            items = FXCollections.observableArrayList(ConsumeFrom.values().map { it.name }.toList())
            valueProperty().bindBidirectional(viewModel.consumeFromProperty)
        }

    private fun EventTarget.deleteButton() =
        confirmationButton("delete", "The topic \"${viewModel.nameProperty.value}\" will be removed.") {
            viewModel.dispatch { delete() }
            closeTab()
        }

    private fun EventTarget.recordsTable() =
        tableview<RecordViewModel> {
            val timeColumn = column("Time", RecordViewModel::timestampProperty) { prefWidthProperty().set(150.0) }
            val keyColumn = column("Key", RecordViewModel::keyProperty) { prefWidthProperty().set(300.0) }
            val valueColumn = column("Value", RecordViewModel::valueProperty) {
                prefWidthProperty().bind(
                    this.tableView.widthProperty()
                        .minus(keyColumn.widthProperty())
                        .minus(timeColumn.widthProperty())
                        .minus(20.0)
                )
            }

            valueColumn.setCellFactory {
                TableCell<RecordViewModel, String>().apply {
                    graphic = text {
                        addClass("text")
                        wrappingWidthProperty().bind(valueColumn.widthProperty().subtract(Bindings.multiply(2.0, graphicTextGapProperty())))
                        textProperty().bind(stringBinding(itemProperty()) { get()?.toString() ?: "" })
                    }
                    prefHeight = Control.USE_COMPUTED_SIZE
                }
            }

            viewModel.configureFilteredRecords(this.comparatorProperty())
            itemsProperty().bind(viewModel.filteredRecords)

            contextMenu = contextmenu {
                item("Copy") { action { viewModel.copySelectedRecordToClipboard() } }
                item("Copy all") { action { viewModel.copyAllRecordsToClipboard() } }
            }

            bindSelected(viewModel.selectedItem)
            selectionModel.selectionMode = SelectionMode.SINGLE

            vgrow = Priority.ALWAYS
            hgrow = Priority.ALWAYS
        }

    override fun onError(throwable: Throwable) {
        close()
    }

    override fun onTabClosed() {
        viewModel.dispatch { stop() }
    }
}
