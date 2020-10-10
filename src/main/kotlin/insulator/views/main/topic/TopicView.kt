package insulator.views.main.topic

import insulator.lib.kafka.ConsumeFrom
import insulator.lib.kafka.DeserializationFormat
import insulator.viewmodel.main.topic.RecordViewModel
import insulator.viewmodel.main.topic.TopicViewModel
import insulator.views.common.InsulatorView
import insulator.views.component.appBar
import insulator.views.component.blueButton
import insulator.views.component.confirmationButton
import insulator.views.component.fieldName
import insulator.views.component.h1
import insulator.views.component.searchBox
import insulator.views.component.subTitle
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

class TopicView : InsulatorView<TopicViewModel>(viewModelClazz = TopicViewModel::class) {

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
                button(viewModel.consumeButtonText) { action { viewModel.consume() } }
                fieldName("from")
                consumeFromCombobox()
                valueFormatOptions()
                button("Clear") { action { viewModel.clear() } }
            }
            right = searchBox(viewModel.searchItem, this@TopicView)
        }
        recordsTable()

        prefWidth = 800.0
        prefHeight = 800.0
    }

    private fun EventTarget.valueFormatOptions() {
        if (viewModel.cluster.isSchemaRegistryConfigured()) {
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
            viewModel.delete()
            close()
        }

    private fun EventTarget.recordsTable() =
        tableview<RecordViewModel> {
            val timeColumn = column("Time", RecordViewModel::timestampProperty) { prefWidthProperty().set(150.0) }
            val keyColumn = column("Key", RecordViewModel::keyProperty) { prefWidthProperty().set(300.0) }
            column("Value", RecordViewModel::valueProperty) {
                prefWidthProperty().bind(
                    this.tableView.widthProperty()
                        .minus(keyColumn.widthProperty())
                        .minus(timeColumn.widthProperty())
                        .minus(20.0)
                )
                setCellFactory {
                    TableCell<RecordViewModel, String>().apply {
                        graphic = text {
                            addClass("text")
                            wrappingWidthProperty().bind(this@column.widthProperty().subtract(Bindings.multiply(2.0, graphicTextGapProperty())))
                            textProperty().bind(stringBinding(itemProperty()) { get()?.toString() ?: "" })
                        }
                        prefHeight = Control.USE_COMPUTED_SIZE
                    }
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

    override fun onDock() {
        currentWindow?.setOnCloseRequest { viewModel.stop() }
        titleProperty.bind(Bindings.createStringBinding({ "${viewModel.nameProperty.value} ${viewModel.cluster.name}" }, viewModel.nameProperty))
        super.onDock()
    }

    override fun onError(throwable: Throwable) {
        close()
    }
}
