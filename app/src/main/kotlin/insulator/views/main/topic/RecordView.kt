package insulator.views.main.topic

import insulator.jsonhelper.JsonFormatter
import insulator.ui.common.InsulatorView
import insulator.ui.component.appBar
import insulator.ui.component.fieldName
import insulator.ui.component.jsonView
import insulator.viewmodel.main.topic.RecordViewModel
import javafx.event.EventTarget
import tornadofx.* // ktlint-disable no-wildcard-imports

class RecordView(
    override val viewModel: RecordViewModel,
    private val formatter: JsonFormatter
) : InsulatorView() {

    override val root = vbox(spacing = 10.0) {
        appBar { title = "Record view" }
        field("Timestamp", "${viewModel.timestampProperty.value} (${viewModel.formattedTimeStampProperty.value})")
        field("Partition", viewModel.partitionProperty.value.toString())
        field("Offset", viewModel.offsetProperty.value.toString())
        field("Key", viewModel.keyProperty.value)

        fieldName("Value")
        jsonView(viewModel.valueProperty, formatter)

        fieldName("Headers")
        textarea(viewModel.formattedHeadersProperty) {
            isEditable = false
        }

        borderpane {
            right = button("Close") {
                action { close() }
            }
        }

        prefWidth = 800.0
        prefHeight = 600.0
    }

    private fun EventTarget.field(title: String, value: String) = with(this) {
        fieldName(title)
        textfield(value) { isEditable = false }
    }
}
