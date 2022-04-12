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
import javax.inject.Inject

class ConfigurationView @Inject constructor(private val viewModel: ConfigurationViewModel) : View() {

    override val root = form {
        h1("Configurations")
        fieldset {
            // Switch between light and dark theme.
            field("Use dark theme") { checkbox(property = viewModel.darkTheme) }
            // Prevents the user to create topics, schemas or produce records.
            field("Readonly mode") { checkbox(property = viewModel.readonlyMode) }
            // Parse avro logical type (e.g. date, timestamp) into human readable strings.
            field("Human readable avro") { checkbox(property = viewModel.humanReadableAvro) }
        }
        borderpane {
            right = closeButton()
        }
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
            it.minWidth = 230.0
            it.width = 230.0
            it.resizableProperty().value = false
        }
        super.onDock()
        title = "Configurations"
    }
}
