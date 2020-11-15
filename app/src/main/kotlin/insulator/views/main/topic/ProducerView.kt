package insulator.views.main.topic

import insulator.di.TopicScope
import insulator.helper.dispatch
import insulator.helper.toObservable
import insulator.kafka.consumer.DeserializationFormat
import insulator.ui.common.InsulatorView
import insulator.ui.component.appBar
import insulator.ui.component.fieldName
import insulator.viewmodel.main.topic.ProducerViewModel
import javafx.beans.binding.Bindings
import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextArea
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import tornadofx.action
import tornadofx.attachTo
import tornadofx.borderpane
import tornadofx.button
import tornadofx.combobox
import tornadofx.enableWhen
import tornadofx.hbox
import tornadofx.label
import tornadofx.onDoubleClick
import tornadofx.scrollpane
import tornadofx.textfield
import tornadofx.vbox
import tornadofx.vgrow
import javax.inject.Inject

@TopicScope
class ProducerView @Inject constructor(
    override val viewModel: ProducerViewModel
) : InsulatorView() {

    private val recordValueTextArea = TextArea()

    override val root = vbox(spacing = 10.0) {
        appBar { title = viewModel.topic.name }
        fieldName("Key")
        textfield(viewModel.keyProperty) { id = "field-producer-key" }

        valueFormatOptions()

        fieldName("Value")
        recordValueTextArea()

        fieldName("Validation")
        validationArea()

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

    private fun EventTarget.valueFormatOptions() {
        if (viewModel.cluster.isSchemaRegistryConfigured()) {
            hbox(alignment = Pos.CENTER_LEFT) {
                fieldName("Serializer")
                combobox<String> {
                    items = DeserializationFormat.values().toObservable()
                    valueProperty().bindBidirectional(viewModel.serializeValueProperty)
                }
            }
        }
    }

    private fun EventTarget.validationArea() =
        scrollpane {
            label {
                val warning = { viewModel.validationErrorProperty.value }
                textProperty().bind(Bindings.createStringBinding({ if (warning().isNullOrEmpty()) "Valid" else warning() }, viewModel.validationErrorProperty))
                textFillProperty().bind(
                    Bindings.createObjectBinding({ if (warning().isNullOrEmpty()) Color.GREEN else Color.RED }, viewModel.validationErrorProperty)
                )
                isWrapText = true
                onDoubleClick { autoComplete() }
            }
            vbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
            minHeight = 15.0
            maxHeight = 100.0
        }

    private fun EventTarget.recordValueTextArea() {
        recordValueTextArea.apply {
            id = "field-producer-value"
            textProperty().bindBidirectional(viewModel.valueProperty)
            vgrow = Priority.ALWAYS
        }.attachTo(this)
    }

    private fun autoComplete() {
        if (!viewModel.nextFieldProperty.value.isNullOrEmpty())
            with(recordValueTextArea) { insertText(caretPosition, "\"${viewModel.nextFieldProperty.value}\":") }
    }

    override fun onDock() {
        title = "Insulator Producer"
        super.onDock()
    }
}
