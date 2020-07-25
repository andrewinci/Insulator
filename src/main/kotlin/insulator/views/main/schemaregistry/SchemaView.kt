package insulator.views.main.schemaregistry

import insulator.Styles
import insulator.viewmodel.main.schemaregistry.SchemaViewModel
import tornadofx.*
import javax.swing.text.html.HTMLEditorKit

class SchemaView : View("Schema registry") {

    private val viewModel: SchemaViewModel by inject()

    override val root = borderpane {
        top = vbox {
            vbox {
                label(viewModel.nameProperty.value) { addClass(Styles.h1) }
                addClass(Styles.topBarMenu)
            }
            hbox { addClass(Styles.topBarMenuShadow) }
//            keyValueLabel("Internal topic", viewModel.internalProperty)
//            keyValueLabel("Partitions count", viewModel.partitionsProperty)
        }
        center = textflow {
            text(viewModel.schemaProperty)
        }
    }

    override fun onDock() {
        super.currentStage?.width = 600.0
        super.currentStage?.height = 600.0
        super.onDock()
    }
}