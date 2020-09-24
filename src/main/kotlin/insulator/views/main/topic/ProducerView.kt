package insulator.views.main.topic

import insulator.styles.Controls
import insulator.styles.Titles
import insulator.viewmodel.main.topic.ProducerViewModel
import insulator.views.common.InsulatorView
import javafx.scene.control.TextArea
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import tornadofx.* // ktlint-disable no-wildcard-imports

class ProducerView : InsulatorView<ProducerViewModel>(viewModelClazz = ProducerViewModel::class, title = "Producer") {

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
            label(viewModel.validationError) {
                textFill = Color.RED
                onDoubleClick {
                    with(recordTextArea) {
                        insertText(caretPosition, "\"${viewModel.nextFieldProperty.value}\":")
                    }
                }
            }
            label("Value")
            recordTextArea.attachTo(this)
            borderpane {
                paddingAll = 20.0
                right = button("Send") {
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
}
