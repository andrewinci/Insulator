package insulator.views.main.schemaregistry

import insulator.styles.Controls
import insulator.styles.Theme
import insulator.styles.Titles
import insulator.viewmodel.main.schemaregistry.SchemaViewModel
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.input.Clipboard
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.Priority
import javafx.scene.text.Font
import tornadofx.* // ktlint-disable no-wildcard-imports

class SchemaView : View("Schema registry") {

    private val viewModel: SchemaViewModel by inject()

    override val root = borderpane {
        top = vbox {
            vbox {
                label(viewModel.nameProperty.value) { addClass(Titles.h1) }
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
                            fill = it.color
                            font = Font.font("Helvetica", it.fontWeight, 15.0)
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

    override fun onDock() {
        super.currentStage?.width = 800.0
        super.currentStage?.height = 800.0
        title = viewModel.nameProperty.value
        super.onDock()
    }
}
