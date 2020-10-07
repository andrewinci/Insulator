package insulator.views.main.schemaregistry

import insulator.lib.jsonhelper.Token
import insulator.lib.kafka.model.Schema
import insulator.views.component.appBar
import insulator.views.component.confirmationButton
import insulator.views.component.h1
import insulator.views.style.Theme
import insulator.viewmodel.main.schemaregistry.SchemaViewModel
import insulator.views.common.InsulatorView
import javafx.beans.binding.Bindings
import javafx.event.EventTarget
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.scene.text.Font
import tornadofx.* // ktlint-disable no-wildcard-imports

class SchemaView : InsulatorView<SchemaViewModel>(viewModelClazz = SchemaViewModel::class) {

    override val root = vbox {
        appBar {
            hbox {
                h1(viewModel.nameProperty.value)
                deleteButton()
            }
        }
        hbox(alignment = Pos.CENTER_LEFT) {
            label("Schema")
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
                        is Token.Symbol -> Color.GRAY
                        is Token.Key -> Color.BLUE
                        is Token.Value -> Color.GREEN
                    }
                    font = Font.font("Helvetica", 15.0)
                }
                res
            }
            contextMenu = contextmenu { item("Copy") { action { viewModel.copySchemaToClipboard() } } }
            background = Background(BackgroundFill(Theme.backgroundColor, CornerRadii.EMPTY, Insets.EMPTY))
            vgrow = Priority.ALWAYS
        }
    }

    override fun onDock() {
        titleProperty.bind(Bindings.createStringBinding({ "${viewModel.nameProperty.value} ${viewModel.cluster.name}" }, viewModel.nameProperty))
        super.onDock()
    }
}
