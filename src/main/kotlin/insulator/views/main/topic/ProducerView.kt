package insulator.views.main.topic

import insulator.styles.Controls
import insulator.styles.Titles
import insulator.viewmodel.main.topic.ProducerViewModel
import insulator.views.common.InsulatorView
import javafx.geometry.Insets
import tornadofx.* // ktlint-disable no-wildcard-imports

class ProducerView : InsulatorView<ProducerViewModel>(viewModelClazz = ProducerViewModel::class, title = "Producer") {

    override val root = form {
        fieldset {
            label(viewModel.topicName) { addClass(Titles.h1) }
            field("Key") { textfield(viewModel.keyProperty) }
            label(viewModel.validationError) {
                prefHeight = 30.0
                isWrapText = true
            }
            field("Value") { textarea(viewModel.valueProperty) }
        }
        borderpane {
            padding = Insets(0.0, 50.0, 0.0, 50.0)
            right = button("Send") {
                action {
                    viewModel.send()
                }
            }
        }
        addClass(Controls.view)
    }
}
