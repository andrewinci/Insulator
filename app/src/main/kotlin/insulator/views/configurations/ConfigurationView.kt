package insulator.views.configurations

import insulator.ui.component.h1
import insulator.viewmodel.configurations.ConfigurationViewModel
import javafx.event.EventTarget
import javafx.stage.Modality
import javafx.stage.StageStyle
import tornadofx.View
import tornadofx.action
import tornadofx.borderpane
import tornadofx.button
import tornadofx.checkbox
import tornadofx.field
import tornadofx.fieldset
import tornadofx.form
import tornadofx.text
import javax.inject.Inject

class ConfigurationView @Inject constructor(private val viewModel: ConfigurationViewModel) : View() {

    override val root = form {
        h1("Configurations")
        fieldset {
            fieldset {
                field("Use dark theme") { checkbox(property = viewModel.darkTheme) }
                field("Readonly mode") { checkbox(property = viewModel.readonlyMode) }
                field("Human readable avro") { checkbox(property = viewModel.humanReadableAvro) }
            }
        }
        borderpane {
            right = closeButton()
        }
        prefWidth = 380.0
    }

    fun show() {
        this.openWindow(modality = Modality.APPLICATION_MODAL, stageStyle = StageStyle.UTILITY)
    }

    private fun EventTarget.closeButton() =
        button {
            text = "Close"
            action {
                close()
            }
        }

    override fun onDock() {
        super.currentStage?.let {
            it.minWidth = 380.0
            it.width = 380.0
            it.resizableProperty().value = false
        }
        super.onDock()
        title = "Configurations"
    }
}
