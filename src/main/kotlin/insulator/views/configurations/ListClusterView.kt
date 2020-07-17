package insulator.views.configurations

import insulator.Styles
import insulator.lib.configuration.ConfigurationRepo
import insulator.viewmodel.configurations.ClusterModel
import insulator.viewmodel.configurations.ClusterViewModel
import insulator.viewmodel.configurations.ListClusterViewModel
import insulator.views.common.ICON_SETTINGS
import insulator.views.main.MainView
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.image.Image
import tornadofx.*
import tornadofx.label
import tornadofx.vbox

class ListClusterView : View("Insulator") {

    private val viewModel: ListClusterViewModel by inject()

    override val root = vbox(alignment = Pos.CENTER, spacing = 15) {
        label("Clusters") { addClass(Styles.h1, Styles.mainColor) }
        listview(viewModel.clustersProperty) {
            cellFormat { cluster ->
                graphic = borderpane {
                    center = vbox {
                        label(cluster.name) { addClass(Styles.h1) }
                        label(cluster.endpoint)
                    }
                    right = vbox(alignment = Pos.CENTER) {
                        button {
                            graphic = imageview(Image(ICON_SETTINGS, 20.0, 20.0, true, true))
                            onMouseClicked = EventHandler {
                                val scope = Scope()
                                setInScope(ClusterViewModel(ClusterModel(cluster)), scope)
                                find<ClusterView>(scope).openWindow()
                            }
                        }
                    }
                    maxHeight = 50.0
                }
                onMouseClicked = EventHandler {
                    ConfigurationRepo.currentCluster = cluster
                    replaceWith(find<MainView>())
                }
            }
        }
        button {
            text = "Add new cluster"
            action { find<ClusterView>().openWindow() }
        }
        paddingAll = 10.0
    }

    override fun onDock() {
        super.onDock()
        setWindowMinSize(380.0, 500.0)
        super.currentStage?.resizableProperty()?.value = false
    }

}