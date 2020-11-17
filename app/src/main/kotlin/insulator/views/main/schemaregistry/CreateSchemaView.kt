package insulator.views.main.schemaregistry

import insulator.helper.dispatch
import insulator.ui.common.InsulatorView
import insulator.ui.component.appBar
import insulator.ui.component.fieldName
import insulator.viewmodel.main.schemaregistry.CreateSchemaViewModel
import javafx.beans.binding.Bindings
import javafx.event.EventTarget
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import tornadofx.action
import tornadofx.borderpane
import tornadofx.button
import tornadofx.enableWhen
import tornadofx.label
import tornadofx.scrollpane
import tornadofx.textarea
import tornadofx.textfield
import tornadofx.vbox
import tornadofx.vgrow
import javax.inject.Inject

class CreateSchemaView @Inject constructor(
    override val viewModel: CreateSchemaViewModel
) : InsulatorView() {

    override val root = vbox(spacing = 10.0) {
        appBar { title = "Register new subject" }
        fieldName("Subject")
        textfield(viewModel.subjectProperty)

        fieldName("Value")
        schemaValueTextArea()

        fieldName("Validation")
        validationArea()

        borderpane {
            right = registerButton()
        }

        prefWidth = 800.0
        prefHeight = 800.0
    }

    private fun EventTarget.validationArea() =
        scrollpane {
            label(viewModel.validationErrorProperty) {
                textFillProperty().bind(
                    Bindings.createObjectBinding({ if (viewModel.validationErrorProperty.value == viewModel.VALID) Color.GREEN else Color.RED }, viewModel.validationErrorProperty)
                )
                isWrapText = true
            }
            vbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
            minHeight = 30.0
            maxHeight = 100.0
        }

    private fun EventTarget.schemaValueTextArea() = textarea {
        id = "field-schema-value"
        textProperty().bindBidirectional(viewModel.schemaProperty)
        vgrow = Priority.ALWAYS
    }

    private fun EventTarget.registerButton() =
        button("Register") {
            id = "button-schema-register"
            enableWhen(viewModel.isSchemaValidProperty)
            action { viewModel.dispatch { register() }; close() }
        }

    override fun onDock() {
        title = "New subject"
        super.onDock()
    }
}
