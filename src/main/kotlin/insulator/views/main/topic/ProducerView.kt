package insulator.views.main.topic

import insulator.styles.Controls
import insulator.ui.component.appBar
import insulator.ui.component.h1
import insulator.viewmodel.main.topic.ProducerViewModel
import insulator.views.common.InsulatorView
import javafx.beans.binding.Bindings
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextArea
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import tornadofx.* // ktlint-disable no-wildcard-imports

class ProducerView : InsulatorView<ProducerViewModel>(viewModelClazz = ProducerViewModel::class) {

    private val recordTextArea = TextArea().apply {
        textProperty().bindBidirectional(viewModel.valueProperty)
        vgrow = Priority.ALWAYS
    }

    override val root = borderpane {
        shortcut("CTRL+SPACE") { autoComplete() }
        top = appBar { h1(viewModel.topicName) }
        center = vbox(spacing = 5.0) {
            label("Key")
            textfield(viewModel.keyProperty)
            recordTextArea.attachTo(this)
            scrollpane {
                label(viewModel.validationErrorProperty) {
                    textFill = Color.RED
                    isWrapText = true
                    onDoubleClick { autoComplete() }
                }
                vbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
                minHeight = 15.0
                maxHeight = 100.0
            }
            borderpane {
                paddingAll = 20.0
                right = button("Send") {
                    enableWhen(viewModel.canSendProperty)
                    action {
                        viewModel.send()
                    }
                }
            }
        }
        addClass(Controls.view)
        prefWidth = 800.0
        prefHeight = 800.0
    }

    private fun autoComplete() {
        if (!viewModel.nextFieldProperty.value.isNullOrEmpty()) {
            with(recordTextArea) {
                insertText(caretPosition, "\"${viewModel.nextFieldProperty.value}\":")
            }
        }
    }

    override fun onDock() {
        titleProperty.bind(
            Bindings.createStringBinding(
                {
                    "${viewModel.cluster.name} ${viewModel.producerTypeProperty.value}"
                },
                viewModel.producerTypeProperty
            )
        )
        super.onDock()
    }
}
