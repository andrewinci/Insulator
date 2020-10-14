package insulator.views.main.topic

import insulator.viewmodel.main.topic.ProducerViewModel
import insulator.views.common.InsulatorView
import insulator.views.component.appBar
import insulator.views.component.fieldName
import insulator.views.component.h1
import javafx.beans.binding.Bindings
import javafx.event.EventTarget
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextArea
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import tornadofx.* // ktlint-disable no-wildcard-imports

class ProducerView : InsulatorView<ProducerViewModel>(viewModelClazz = ProducerViewModel::class) {

    private val recordValueTextArea = TextArea()

    override val root = vbox(spacing = 10.0) {
        appBar { h1(viewModel.topicName) }

        fieldName("Key")
        textfield(viewModel.keyProperty)

        fieldName("Value")
        recordValueTextArea()

        fieldName("Validation")
        validationArea()

        borderpane {
            right = button("Send") {
                enableWhen(viewModel.canSendProperty)
                action { viewModel.send() }
            }
        }

        shortcut("CTRL+SPACE") { autoComplete() }
        prefWidth = 800.0
        prefHeight = 800.0
    }

    private fun EventTarget.validationArea() =
        scrollpane {
            label {
                val warning = {
                    viewModel.validationErrorProperty.value
                }
                textProperty().bind(Bindings.createStringBinding({ if (warning().isNullOrEmpty()) "Valid" else warning() }, viewModel.validationErrorProperty))
                textFillProperty().bind(
                    Bindings.createObjectBinding(
                        {
                            if (warning().isNullOrEmpty()) {
                                Color.GREEN
                            } else Color.RED
                        },
                        viewModel.validationErrorProperty
                    )
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
            textProperty().bindBidirectional(viewModel.valueProperty)
            vgrow = Priority.ALWAYS
        }.attachTo(this)
    }

    private fun autoComplete() {
        if (!viewModel.nextFieldProperty.value.isNullOrEmpty()) {
            with(recordValueTextArea) {
                insertText(caretPosition, "\"${viewModel.nextFieldProperty.value}\":")
            }
        }
    }

    override fun onDock() {
        titleProperty.bind(
            Bindings.createStringBinding({ "Insulator - ${viewModel.producerTypeProperty.value} producer" }, viewModel.producerTypeProperty)
        )
        super.onDock()
    }
}
