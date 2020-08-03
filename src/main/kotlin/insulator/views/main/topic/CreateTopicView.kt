package insulator.views.main.topic

import insulator.styles.Controls
import insulator.styles.Titles
import insulator.viewmodel.main.topic.CreateTopicViewModel
import javafx.geometry.Insets
import tornadofx.* // ktlint-disable no-wildcard-imports

class CreateTopicView : View() {
    private val viewModel: CreateTopicViewModel by inject()

    override val root = form {
        fieldset {
            label("Create topic") { addClass(Titles.h1) }
            field("Topic name") { textfield(viewModel.nameProperty).required() }
            field("Number of partitions") {
                textfield(viewModel.partitionCountProperty) {
                    filterInput { it.controlNewText.isInt() }
                    validator {
                        ValidationMessage(
                            "",
                            if (it?.toShortOrNull() ?: 0 > 0) ValidationSeverity.Success else ValidationSeverity.Error
                        )
                    }
                    required()
                }
            }
            field("Replication factory") {
                textfield(viewModel.replicationFactorProperty) {
                    filterInput { it.controlNewText.isInt() }
                    validator {
                        ValidationMessage(
                            "",
                            if (it?.toShortOrNull() ?: 0 > 0) ValidationSeverity.Success else ValidationSeverity.Error
                        )
                    }
                    required()
                }
            }
        }

        borderpane {
            padding = Insets(0.0, 50.0, 0.0, 50.0)
            right = button("Create") {
                enableWhen(viewModel.valid)
                action {
                    viewModel.commit()
                    viewModel.save()
                    close()
                }
            }
        }
        addClass(Controls.view)
    }
}
