package insulator.views.main.schemaregistry

import insulator.lib.jsonhelper.Token
import insulator.styles.Controls
import insulator.styles.Theme
import insulator.styles.Titles
import insulator.viewmodel.main.schemaregistry.SchemaViewModel
import insulator.views.common.InsulatorView
import insulator.views.common.confirmationButton
import javafx.beans.binding.Bindings
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
import java.util.concurrent.Callable

class SchemaView : InsulatorView<SchemaViewModel>(viewModelClazz = SchemaViewModel::class) {

    override val root = borderpane {
        top = vbox {
            hbox {
                label(viewModel.nameProperty.value) { addClass(Titles.h1) }
                confirmationButton("delete", "The schema \"${viewModel.nameProperty.value}\" will be removed.") {
                    viewModel.delete()
                    close()
                }
                addClass(Controls.topBarMenu)
            }
            hbox { addClass(Controls.topBarMenuShadow) }
        }
        center = vbox(spacing = 2.0) {
            hbox(alignment = Pos.CENTER_LEFT) {
                label("Schema version")
                combobox<Int> {
                    items.bind(viewModel.versionsProperty) { it }
                    valueProperty().bindBidirectional(viewModel.selectedVersionProperty)
                }
            }
            scrollpane {
                schemaContent()
                vgrow = Priority.ALWAYS
            }
        }
        addClass(Controls.view)
        prefWidth = 800.0
        prefHeight = 800.0
    }

    private fun ScrollPane.schemaContent() = apply {
        textflow {
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
        titleProperty.bind(Bindings.createStringBinding(Callable { "${viewModel.cluster.name}  ${viewModel.nameProperty.value}" }, viewModel.nameProperty))
        super.onDock()
    }
}
