package insulator.views.configurations

import insulator.Styles
import insulator.di.GlobalConfiguration
import insulator.viewmodel.configurations.ClusterModel
import insulator.viewmodel.configurations.ClusterViewModel
import insulator.viewmodel.configurations.ListClusterViewModel
import insulator.views.common.ICON_SETTINGS_SVG
import insulator.views.main.MainView
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.image.Image
import tornadofx.*
import tornadofx.label
import tornadofx.vbox

class ListClusterView : View("Insulator") {

    private val viewModel: ListClusterViewModel by inject()

    override val root = vbox(spacing = 15) {
        label("Clusters") { addClass(Styles.h1) }
        listview(viewModel.clustersProperty) {
            cellFormat { cluster ->
                graphic = borderpane {
                    center = vbox(alignment = Pos.CENTER_LEFT) {
                        label(cluster.name) { addClass(Styles.h2) }
                        label(cluster.endpoint){ addClass(Styles.h3) }
                    }
                    right = vbox(alignment = Pos.CENTER) {
                        button {
                            graphic = SVGIcon(ICON_SETTINGS_SVG, 18)
                            onMouseClicked = EventHandler {
                                val scope = Scope()
                                setInScope(ClusterViewModel(ClusterModel(cluster)), scope)
                                find<ClusterView>(scope).openWindow()
                            }
                        }
                    }
                    maxHeight = 80.0
                }
                onMouseClicked = EventHandler {
                    GlobalConfiguration.currentCluster = cluster
                    val scope = Scope()
                    setInScope(ClusterViewModel(ClusterModel(cluster)), scope)
                    find<MainView>(scope).openWindow(owner = null)
                    close()
                }
            }
        }
        hbox(alignment = Pos.CENTER_RIGHT){
            button {
                alignment = Pos.CENTER_RIGHT
                text = "Add new cluster"
                action { find<ClusterView>().openWindow() }
            }
        }

        addClass(Styles.clusterListView)
    }

    override fun onDock() {
        super.onDock()
        setWindowMinSize(380.0, 500.0)
        setWindowMaxSize(380.0, 500.0)
        super.currentStage?.resizableProperty()?.value = false
    }

}