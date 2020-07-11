package insulator.views.configurations

import insulator.views.common.title
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.text.FontWeight
import tornadofx.*

class ConfigurationView(clusterName: String) : View("Cluster name") {
    override val root = vbox(alignment = Pos.CENTER, spacing = 15) {
        padding = Insets(10.0)
        title(clusterName)
        label {
            text = "Sample"
        }
    }
}