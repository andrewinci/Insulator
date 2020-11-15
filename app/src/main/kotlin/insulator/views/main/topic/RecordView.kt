package insulator.views.main.topic

import insulator.jsonhelper.JsonFormatter
import insulator.jsonhelper.Token
import insulator.ui.common.InsulatorView
import insulator.ui.component.appBar
import insulator.ui.component.fieldName
import insulator.ui.style.theme
import insulator.viewmodel.main.topic.RecordViewModel
import javafx.event.EventTarget
import javafx.scene.layout.Priority
import javafx.scene.text.Font
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
        scrollpane {
            textflow {
                children.setAll(
                    formatter.formatJsonString(viewModel.valueProperty.value)
                        .map { records ->
                            records.map {
                                val res = text(it.text) {
                                    fill = when (it) {
                                        is Token.Symbol -> theme.darkGray
                                        is Token.Key -> theme.blueColor
                                        is Token.Value -> theme.greenColor
                                    }
                                    font = Font.font("Helvetica", 15.0)
                                }
                                res
                            }
                        }.fold({ listOf(text(viewModel.valueProperty)) }, { it })
                )
            }
            vgrow = Priority.ALWAYS
            minHeight = 50.0
        }

        fieldName("Headers")
        textarea(viewModel.headersProperty.value.map { (key, value) -> "${key}=${value}" }.joinToString("\n")) {
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