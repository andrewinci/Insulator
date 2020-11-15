package insulator.views.main.schemaregistry

import insulator.di.SubjectScope
import insulator.jsonhelper.Token
import insulator.kafka.model.Schema
import insulator.ui.common.InsulatorTabView
import insulator.ui.component.appBar
import insulator.ui.component.confirmationButton
import insulator.ui.component.fieldName
import insulator.ui.style.theme
import insulator.viewmodel.main.schemaregistry.SchemaViewModel
import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Priority
import javafx.scene.text.Font
import tornadofx.action
import tornadofx.bind
import tornadofx.combobox
import tornadofx.contextmenu
import tornadofx.hbox
import tornadofx.hgrow
import tornadofx.item
import tornadofx.scrollpane
import tornadofx.text
import tornadofx.textflow
import tornadofx.vbox
import tornadofx.vgrow
import javax.inject.Inject

@SubjectScope
class SchemaView @Inject constructor(
    override val viewModel: SchemaViewModel
) : InsulatorTabView() {

    override val root = vbox {
        appBar {
            title = viewModel.nameProperty.value
            buttons = listOf(deleteButton())
        }
        hbox(alignment = Pos.CENTER_LEFT) {
            fieldName("Schema")
            schemaComboBox()
        }
        scrollpane {
            schemaContent()
            vgrow = Priority.ALWAYS
        }
    }

    private fun EventTarget.schemaComboBox() =
        combobox<Schema> {
            items.bind(viewModel.versionsProperty) { it }
            valueProperty().bindBidirectional(viewModel.selectedVersionProperty)
            cellFormat { text = "v: ${it.version} id: ${it.id}" }
        }

    private fun EventTarget.deleteButton() =
        confirmationButton("Delete", "The schema \"${viewModel.nameProperty.value}\" will be removed.") {
            viewModel.delete()
            closeTab()
        }

    private fun ScrollPane.schemaContent() = apply {
        textflow {
            // todo: move to controls
            children.bind(viewModel.schemaProperty) {
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
            contextMenu = contextmenu { item("Copy") { action { viewModel.copySchemaToClipboard() } } }
            vgrow = Priority.ALWAYS
            hgrow = Priority.ALWAYS
        }
    }
}
