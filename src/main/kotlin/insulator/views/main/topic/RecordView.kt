package insulator.views.main.topic

import insulator.lib.jsonhelper.JsonFormatter
import insulator.lib.jsonhelper.Token
import insulator.lib.kafka.model.Record
import insulator.ui.component.fieldName
import insulator.ui.style.theme
import javafx.scene.text.Font
import tornadofx.* // ktlint-disable no-wildcard-imports
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class RecordView(private val record: Record) : View() {

    private val formatter: JsonFormatter by di()
    private var dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("UTC"))

    override val root = vbox(spacing = 10.0) {

        fieldName("Key")
        textfield(record.key) {
            isEditable = false
        }

        fieldName("Timestamp")
        textfield(dateTimeFormatter.format(Instant.ofEpochMilli(record.timestamp))) {
            isEditable = false
        }

        fieldName("Value")
        scrollpane {
            textflow {
                children.setAll(
                    formatter.formatJsonString(record.value)
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
                        }.fold({ listOf(text(record.value)) }, { it })
                )
            }
        }

        fieldName("Headers")
        textarea(record.headers.map { "${it.key}=${it.value}" }.joinToString("\n")) {
            isEditable = false
            minHeight = 100.0
        }

        borderpane {
            right = button("Close") {
                action { close() }
            }
        }

        prefWidth = 800.0
        prefHeight = 800.0
    }
}
