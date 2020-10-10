package insulator.views.main.schemaregistry

import insulator.lib.jsonhelper.Token
import insulator.lib.kafka.model.Schema
import insulator.viewmodel.main.schemaregistry.SchemaViewModel
import insulator.views.common.InsulatorView
import insulator.views.component.appBar
import insulator.views.component.confirmationButton
import insulator.views.component.fieldName
import insulator.views.component.h1
import insulator.views.style.theme
import javafx.beans.binding.Bindings
import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Priority
import javafx.scene.text.Font
import tornadofx.* // ktlint-disable no-wildcard-imports

class SchemaView : InsulatorView<SchemaViewModel>(viewModelClazz = SchemaViewModel::class) {

    override val root = vbox {
        appBar {
            hbox(alignment = Pos.CENTER_LEFT, spacing = 5.0) {
                h1(viewModel.nameProperty.value)
                deleteButton()
            }
        }
        hbox(alignment = Pos.CENTER_LEFT) {
            fieldName("Schema")
            schemaComboBox()
        }
        scrollpane {
            schemaContent()
            vgrow = Priority.ALWAYS
        }

        prefWidth = 800.0
        prefHeight = 800.0
    }

    private fun EventTarget.schemaComboBox() =
        combobox<Schema> {
            items.bind(viewModel.versionsProperty) { it }
            valueProperty().bindBidirectional(viewModel.selectedVersionProperty)
            cellFormat { text = "v: ${it.version} id: ${it.id}" }
        }

    private fun EventTarget.deleteButton() =
        confirmationButton("delete", "The schema \"${viewModel.nameProperty.value}\" will be removed.") {
            viewModel.delete()
            close()
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

    override fun onDock() {
        titleProperty.bind(Bindings.createStringBinding({ "${viewModel.nameProperty.value} ${viewModel.cluster.name}" }, viewModel.nameProperty))
        super.onDock()
    }
}
