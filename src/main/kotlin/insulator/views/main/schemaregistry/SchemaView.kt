package insulator.views.main.schemaregistry

import insulator.lib.jsonhelper.Token
import insulator.styles.Controls
import insulator.styles.Theme
import insulator.styles.Titles
import insulator.viewmodel.main.schemaregistry.SchemaViewModel
import javafx.event.EventTarget
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.input.Clipboard
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.scene.text.Font
import tornadofx.* // ktlint-disable no-wildcard-imports

class SchemaView : View("Schema registry") {

    private val viewModel: SchemaViewModel by inject()

    override val root = borderpane {
        top = vbox {
            hbox {
                label(viewModel.nameProperty.value) { addClass(Titles.h1) }
                deleteButton()
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
                    contextMenu = contextmenu {
                        item("Copy") {
                            action {
                                val content = Clipboard.getSystemClipboard()
                                content.putString(viewModel.schemaProperty.joinToString(separator = "") { it.text })
                            }
                        }
                    }
                    background = Background(BackgroundFill(Theme.backgroundColor, CornerRadii.EMPTY, Insets.EMPTY))
                    vgrow = Priority.ALWAYS
                }
                vgrow = Priority.ALWAYS
            }
        }
        addClass(Controls.view)
    }

    private fun EventTarget.deleteButton() = apply {
        button("Delete") {
            addClass(Controls.alertButton)
            action {
                val closeWindow = { close() }
                alert(
                    Alert.AlertType.WARNING,
                    "All versions of the schema \"${viewModel.nameProperty.value}\" will be removed.", null,
                    ButtonType.CANCEL, ButtonType.OK,
                    owner = currentWindow,
                    actionFn = { buttonType ->
                        when (buttonType) {
                            ButtonType.OK -> {
                                //todo: actually remove the schema
                                closeWindow()
                            }
                            else -> Unit
                        }
                    }
                )
            }
        }
    }

    override fun onDock() {
        super.currentStage?.width = 800.0
        super.currentStage?.height = 800.0
        title = viewModel.nameProperty.value
        super.onDock()
    }
}
