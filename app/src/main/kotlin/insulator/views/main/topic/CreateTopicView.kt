package insulator.views.main.topic

import insulator.lib.helpers.dispatch
import insulator.ui.component.h1
import insulator.ui.component.insulatorAlert
import insulator.viewmodel.main.topic.CreateTopicViewModel
import javafx.event.EventTarget
import javafx.scene.control.Alert
import tornadofx.* // ktlint-disable no-wildcard-imports

class CreateTopicView : View() {
    private val viewModel: CreateTopicViewModel by inject()

    override val root = form {
        fieldset {
            h1("Create topic")
            field("Topic name") { textfield(viewModel.nameProperty).required() }
            field("Number of partitions") { numberOfPartitionTextField() }
            field("Replication factor") { replicationFactorTextField() }
            field("Compacted") { checkbox(property = viewModel.isCompactedProperty) }
        }
        borderpane {
            right = createButton()
        }
    }

    private fun EventTarget.createButton() =
        button("Create") {
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

    private fun EventTarget.replicationFactorTextField() =
        textfield(viewModel.replicationFactorProperty) {
            filterInput { it.controlNewText.isInt() }
            validator { validationMessage(it) }
            required()
        }

    private fun EventTarget.numberOfPartitionTextField() =
        textfield(viewModel.partitionCountProperty) {
            filterInput { it.controlNewText.isInt() }
            validator { validationMessage(it) }
            required()
        }

    private fun validationMessage(it: String?) =
        ValidationMessage(
            null,
            if (it?.toShortOrNull() ?: 0 > 0) ValidationSeverity.Success else ValidationSeverity.Error
        )
}