package insulator.views.main.schemaregistry

import insulator.Styles
import insulator.viewmodel.main.schemaregistry.ListSchemaViewModel
import insulator.viewmodel.main.schemaregistry.SchemaViewModel
import insulator.viewmodel.main.topic.TopicViewModel
import insulator.views.main.topic.TopicView
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.SelectionMode
import javafx.scene.layout.Priority
import tornadofx.*

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
        center = textarea(viewModel.schemaProperty) {
            isEditable = false
        }
    }

    override fun onDock() {
        super.currentStage?.width = 800.0
        super.currentStage?.height = 800.0
        super.onDock()
    }
}