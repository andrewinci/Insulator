package insulator.views.main

import insulator.di.ClusterScope
import insulator.helper.dispatch
import insulator.kafka.model.Cluster
import insulator.ui.ThemeHelper
import insulator.ui.common.InsulatorView
import insulator.ui.component.h1
import insulator.ui.component.h2
import insulator.ui.component.themeButton
import insulator.ui.style.ButtonStyle.Companion.alertButton
import insulator.ui.style.MainViewStyle
import insulator.viewmodel.main.MainViewModel
import insulator.viewmodel.main.TabViewModel
import insulator.views.configurations.ClusterView
import insulator.views.main.consumergroup.ListConsumerGroupView
import insulator.views.main.schemaregistry.ListSchemaView
import insulator.views.main.topic.ListTopicView
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.geometry.Side
import javafx.scene.Parent
import javafx.scene.image.Image
import tornadofx.action
import tornadofx.addClass
import tornadofx.bind
import tornadofx.borderpane
import tornadofx.button
import tornadofx.hbox
import tornadofx.imageview
import tornadofx.onChange
import tornadofx.splitpane
import tornadofx.tabpane
import tornadofx.vbox
import javax.inject.Inject

private const val ICON_TOPICS = "icons/topics.png"
private const val ICON_REGISTRY = "icons/schemaRegistryIcon.png"
private const val ICON_CONSUMERS = "icons/consumerGroupsIcon.png"

const val SIDEBAR_WIDTH = 250.0
const val CONTENT_LIST_WIDTH = 450.0
const val CONTENT_WIDTH = 780.0

@ClusterScope
class MainView @Inject constructor(
    override val viewModel: MainViewModel,
    private val tabViewModel: TabViewModel,
    private val clusterView: ClusterView,
    val cluster: Cluster,
    private val themeHelper: ThemeHelper
) : InsulatorView("Insulator") {

    private val contentList = borderpane {
        centerProperty().bind(viewModel.contentList)
        addClass(MainViewStyle.contentList)
        minWidth = CONTENT_LIST_WIDTH
    }

    private val content = tabpane {
        tabViewModel.contentTabs = tabs
        minWidth = CONTENT_WIDTH
        side = Side.BOTTOM
        addClass(MainViewStyle.content)
    }

    private val nodes: ObservableList<Parent> = FXCollections.observableArrayList(
        sidebar(),
        contentList
    )

    override val root = splitpane { items.bind(nodes) { it } }

    init {
        tabViewModel.contentTabs.onChange {
            when {
                tabViewModel.contentTabs.size == 0 -> {
                    val contentListWidth = contentList.width
                    nodes.removeAt(2)
                    super.currentStage?.width = SIDEBAR_WIDTH + contentListWidth
                }
                nodes.size <= 2 -> nodes.add(content)
                else -> nodes[2] = content
            }
        }
        nodes.onChange { setSize() }
    }

    private fun sidebar() =
        borderpane {
            top = vbox {
                h1(cluster.name)
                hbox {
                    button("Info") { action { clusterView.show(false) }; id = "button-cluster-info" }
                    button("Change cluster") { action { close() }; addClass(alertButton); id = "button-change-cluster" }
                    addClass(MainViewStyle.clusterOptions)
                }
            }
            center = vbox {
                menuItem("Topics", ICON_TOPICS, "sidebar-item-topics") { viewModel.setContentList(ListTopicView::class, currentWindow) }
                menuItem("Schema Registry", ICON_REGISTRY, "sidebar-item-schema-registry") { viewModel.setContentList(ListSchemaView::class, currentWindow) }
                menuItem("Consumer Groups", ICON_CONSUMERS, "sidebar-item-consumer-group") { viewModel.setContentList(ListConsumerGroupView::class, currentWindow) }
            }
            bottom = hbox(alignment = Pos.CENTER) {
                themeButton { themeHelper.dispatch { changeTheme() } }
            }
            addClass(MainViewStyle.sidebar)
            minWidth = SIDEBAR_WIDTH
            maxWidth = SIDEBAR_WIDTH
        }

    private fun EventTarget.menuItem(name: String, icon: String, id: String, onClick: () -> Unit) =
        hbox(spacing = 5.0) {
            imageview(Image(icon)) { fitHeight = 35.0; fitWidth = 35.0; }
            h2(name)
            onMouseClicked = EventHandler { onClick() }
            addClass(MainViewStyle.sidebarItem)
            this.id = id
        }

    private fun setSize() {
        val newMinWidth = SIDEBAR_WIDTH + CONTENT_LIST_WIDTH + if (nodes.size == 2) 0.0 else CONTENT_WIDTH
        super.currentStage?.let { it.minWidth = newMinWidth }
    }

    override fun onDock() {
        super.currentStage?.let {
            it.height = 800.0
            it.isResizable = true
        }
        setSize()
        super.onDock()
    }

    override fun onError(throwable: Throwable) {
        close()
    }
}
