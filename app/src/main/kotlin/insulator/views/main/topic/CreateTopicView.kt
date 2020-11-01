package insulator.views.main.topic

import insulator.helper.dispatch
import insulator.ui.component.h1
import insulator.ui.component.insulatorAlert
import insulator.viewmodel.main.topic.CreateTopicViewModel
import javafx.event.EventTarget
import javafx.scene.control.Alert
import tornadofx.ValidationMessage
import tornadofx.ValidationSeverity
import tornadofx.View
import tornadofx.action
import tornadofx.borderpane
import tornadofx.button
import tornadofx.checkbox
import tornadofx.enableWhen
import tornadofx.field
import tornadofx.fieldset
import tornadofx.filterInput
import tornadofx.form
import tornadofx.isInt
import tornadofx.required
import tornadofx.textfield
import tornadofx.validator
import javax.inject.Inject

class CreateTopicView @Inject constructor(val viewModel: CreateTopicViewModel) : View() {

    override val root = form {
        fieldset {
            h1("Create topic")
            field("Topic name") { textfield(viewModel.nameProperty) { id = "field-create-topic-name" }.required() }
            field("Number of partitions") { numberOfPartitionTextField("field-create-topic-number-of-partitions") }
            field("Replication factor") { replicationFactorTextField("field-create-topic-replication-factor") }
            field("Compacted") { checkbox(property = viewModel.isCompactedProperty) }
        }
        borderpane {
            right = createButton()
        }
    }

    private fun EventTarget.createButton() =
        button("Create") {
            id = "button-create-topic"
            enableWhen(viewModel.valid)
            action {
                viewModel.commit()
                viewModel.dispatch {
                    save().fold(
                        { insulatorAlert(Alert.AlertType.WARNING, it.message ?: "Unable to create the topic.") },
                        { close() }
                    )
                }
            }
        }

    private fun EventTarget.replicationFactorTextField(id: String = "") =
        textfield(viewModel.replicationFactorProperty) {
            this.id = id
            filterInput { it.controlNewText.isInt() }
            validator { validationMessage(it) }
            required()
        }

    private fun EventTarget.numberOfPartitionTextField(id: String = "") =
        textfield(viewModel.partitionCountProperty) {
            this.id = id
            filterInput { it.controlNewText.isInt() }
            validator { validationMessage(it) }
            required()
        }

    private fun validationMessage(it: String?) =
        ValidationMessage(
            null,
            if (it?.toShortOrNull() ?: 0 > 0) ValidationSeverity.Success else ValidationSeverity.Error
        )

    override fun onDock() {
        this.title = "Create new topic"
        super.onDock()
    }
}
