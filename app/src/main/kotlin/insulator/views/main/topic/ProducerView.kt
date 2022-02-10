package insulator.views.main.topic

import com.sun.javafx.collections.ImmutableObservableList
import com.sun.javafx.collections.ObservableListWrapper
import insulator.di.TopicScope
import insulator.helper.dispatch
import insulator.helper.toObservable
import insulator.kafka.producer.SerializationFormat
import insulator.kafka.model.Schema
import insulator.ui.common.InsulatorView
import insulator.ui.component.appBar
import insulator.ui.component.fieldName
import insulator.viewmodel.main.topic.ProducerViewModel
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleListProperty
import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextArea
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import tornadofx.*
import javax.inject.Inject

@TopicScope
class ProducerView @Inject constructor(
    override val viewModel: ProducerViewModel
) : InsulatorView() {

    init {
        viewModel.isTombstoneProperty.onChange { resize() }
    }

    private val recordValueTextArea = TextArea()

    override val root = vbox(spacing = 15.0) {
        appBar { title = viewModel.topic.name }
        fieldName("Key")
        textfield(viewModel.keyProperty) { id = "field-producer-key" }

        valueFormatOptions()?.hideIfTombstone()

        hbox(spacing = 20.0, Pos.CENTER_LEFT) {
            fieldName("Value")
            checkbox("Tombstone", viewModel.isTombstoneProperty)
        }
        recordValueTextArea().hideIfTombstone()

        validationArea().hideIfTombstone()

        borderpane {
            right = button("Send") {
                id = "button-producer-send"
                enableWhen(viewModel.canSendProperty)
                action { viewModel.dispatch { send() }; close() }
            }
        }

        shortcut("CTRL+SPACE") { autoComplete() }
        prefWidth = 800.0
        prefHeight = 800.0
    }

    private fun Node.hideIfTombstone() = apply {
        visibleWhen { viewModel.isTombstoneProperty.not() }
        managedWhen { viewModel.isTombstoneProperty.not() }
    }

    private fun EventTarget.valueFormatOptions() =
        if (viewModel.cluster.isSchemaRegistryConfigured()) {
            hbox(alignment = Pos.CENTER_LEFT) {
                fieldName("Serializer")
                combobox<SerializationFormat> {
                    items = ObservableListWrapper(SerializationFormat.values().toList())
                    valueProperty().bindBidirectional(viewModel.serializationFormatProperty)
                }
            }
        } else null

    private fun EventTarget.validationArea() =
        vbox {
            fieldName("Validation")
            if (viewModel.cluster.isSchemaRegistryConfigured()) {
                hbox(alignment = Pos.CENTER_LEFT) {
                    fieldName("Schema version")
                    combobox<Schema> {
                        id = "combobox-schema-version"
                        items.bind(viewModel.versionsProperty) { it }
                        valueProperty().bindBidirectional(viewModel.selectedVersionProperty)
                        cellFormat { text = "v: ${it.version} id: ${it.id}" }
                    }
                }.visibleWhen(viewModel.serializationFormatProperty.isEqualTo(SerializationFormat.Avro))
            }
            scrollpane {
                label {
                    val warning = { viewModel.validationErrorProperty.value }
                    textProperty().bind(
                        Bindings.createStringBinding(
                            { if (warning().isNullOrEmpty()) "Valid" else warning() },
                            viewModel.validationErrorProperty
                        )
                    )
                    textFillProperty().bind(
                        Bindings.createObjectBinding(
                            { if (warning().isNullOrEmpty()) Color.GREEN else Color.RED },
                            viewModel.validationErrorProperty
                        )
                    )
                    isWrapText = true
                    onDoubleClick { autoComplete() }
                }
                vbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
                minHeight = 50.0
                maxHeight = 100.0
            }
        }

    private fun EventTarget.recordValueTextArea() =
        recordValueTextArea.apply {
            id = "field-producer-value"
            textProperty().bindBidirectional(viewModel.valueProperty)
            vgrow = Priority.ALWAYS
        }.attachTo(this)

    private fun autoComplete() {
        if (!viewModel.nextFieldProperty.value.isNullOrEmpty())
            with(recordValueTextArea) { insertText(caretPosition, "\"${viewModel.nextFieldProperty.value}\":") }
    }

    private fun resize() = currentStage?.let {
        if (viewModel.isTombstoneProperty.value) {
            it.minHeight = 260.0
            it.height = 260.0
            it.maxHeight = 260.0
        } else {
            it.minHeight = 800.0
            it.maxHeight = Double.MAX_VALUE
        }
    }

    override fun onDock() {
        title = "Insulator Producer"
        resize()
        super.onDock()
    }
}
