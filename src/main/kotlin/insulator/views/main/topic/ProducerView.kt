package insulator.views.main.topic

import insulator.styles.Controls
import insulator.styles.Titles
import insulator.viewmodel.main.topic.ProducerViewModel
import insulator.views.common.InsulatorView
import javafx.beans.binding.Bindings
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
        top = vbox {
            hbox {
                label(viewModel.topicName) { addClass(Titles.h1) }
                addClass(Controls.topBarMenu)
            }
            hbox { addClass(Controls.topBarMenuShadow) }
        }
        center = vbox(spacing = 5.0) {
            label("Key")
            textfield(viewModel.keyProperty)
            label(viewModel.validationErrorProperty) {
                textFill = Color.RED
                onDoubleClick {
                    with(recordTextArea) {
                        insertText(caretPosition, "\"${viewModel.nextFieldProperty.value}\":")
                    }
                }
            }
            recordTextArea.attachTo(this)
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
