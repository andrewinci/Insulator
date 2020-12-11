package insulator.views.main.schemaregistry

import insulator.di.SubjectScope
import insulator.helper.hideOnReadonly
import insulator.jsonhelper.JsonFormatter
import insulator.kafka.model.Schema
import insulator.ui.common.InsulatorTabView
import insulator.ui.component.appBar
import insulator.ui.component.confirmationButton
import insulator.ui.component.fieldName
import insulator.ui.component.jsonView
import insulator.ui.component.refreshButton
import insulator.viewmodel.main.schemaregistry.SchemaViewModel
import javafx.event.EventTarget
import javafx.geometry.Pos
import tornadofx.bind
import tornadofx.combobox
import tornadofx.hbox
import tornadofx.text
import tornadofx.vbox
import javax.inject.Inject

@SubjectScope
class SchemaView @Inject constructor(
    override val viewModel: SchemaViewModel,
    private val formatter: JsonFormatter
) : InsulatorTabView() {

    override val root = vbox {
        appBar {
            title = viewModel.nameProperty.value
            buttons = listOf(deleteButton(), refreshButton("schema", viewModel::refresh))
        }
        hbox(alignment = Pos.CENTER_LEFT) {
            fieldName("Schema")
            schemaComboBox()
        }
        jsonView(viewModel.schemaProperty, formatter)
    }

    private fun EventTarget.schemaComboBox() =
        combobox<Schema> {
            id = "combobox-schema-version"
            items.bind(viewModel.versionsProperty) { it }
            valueProperty().bindBidirectional(viewModel.selectedVersionProperty)
            cellFormat { text = "v: ${it.version} id: ${it.id}" }
        }

    private fun EventTarget.deleteButton() =
        confirmationButton("Delete", "The schema \"${viewModel.nameProperty.value}\" will be removed.") {
            viewModel.delete()
            closeTab()
        }.hideOnReadonly()
}
