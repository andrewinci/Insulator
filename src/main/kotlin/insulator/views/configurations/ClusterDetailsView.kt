package insulator.views.configurations

import insulator.model.Cluster
import insulator.views.common.title
import javafx.geometry.Insets
import javafx.geometry.Pos
import tornadofx.*

class ClusterDetailsView(cluster: Cluster) : View(cluster.name) {
    override val root = vbox(alignment = Pos.CENTER, spacing = 15) {
        padding = Insets(10.0)
        title(cluster.name)
        label {
            text = "Sample"
        }
        fieldset {
            field("Sample") {

            }
        }
    }
}