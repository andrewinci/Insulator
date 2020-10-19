package insulator.views.main

import insulator.lib.configuration.model.Cluster
import insulator.ui.common.InsulatorView
import insulator.ui.component.h1
import insulator.ui.component.h2
import insulator.ui.component.themeButton
import insulator.ui.style.MainViewStyle
import insulator.ui.style.changeTheme
import insulator.viewmodel.main.MainViewModel
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
import tornadofx.* // ktlint-disable no-wildcard-imports

private const val ICON_TOPICS = "icons/topics.png"
private const val ICON_REGISTRY = "icons/schemaRegistryIcon.png"
const val SIDEBAR_WIDTH = 250.0
const val CONTENT_LIST_WIDTH = 450.0
const val CONTENT_WIDTH = 780.0

class MainView : InsulatorView<MainViewModel>("Insulator", MainViewModel::class) {

    val cluster: Cluster by di()

    private val contentList = borderpane {
        centerProperty().bind(viewModel.contentList)
        addClass(MainViewStyle.contentList)
        minWidth = CONTENT_LIST_WIDTH
    }

    private val content = tabpane {
        viewModel.contentTabs = tabs
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
        viewModel.contentTabs.onChange {
            when {
                viewModel.contentTabs.size == 0 -> {
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
            top = vbox(alignment = Pos.TOP_CENTER) {
                h1(cluster.name)
                button("Change cluster") { action { close() } }
            }
            center = vbox {
                menuItem("Topics", ICON_TOPICS) { viewModel.setContentList(ListTopicView::class) }
                menuItem("Schema Registry", ICON_REGISTRY) { viewModel.setContentList(ListSchemaView::class) }
            }
            bottom = hbox(alignment = Pos.CENTER) {
                themeButton { changeTheme() }
            }
            addClass(MainViewStyle.sidebar)
            minWidth = SIDEBAR_WIDTH
            maxWidth = SIDEBAR_WIDTH
        }

    private fun EventTarget.menuItem(name: String, icon: String, onClick: () -> Unit) =
        hbox(spacing = 5.0) {
            imageview(Image(icon)) { fitHeight = 35.0; fitWidth = 35.0; }
            h2(name)
            onMouseClicked = EventHandler { onClick() }
            addClass(MainViewStyle.sidebarItem)
        }

    private fun setSize() {
        val newMinWidth = SIDEBAR_WIDTH + CONTENT_LIST_WIDTH + if (nodes.size == 2) 0.0 else CONTENT_WIDTH
        super.currentStage?.let {
            if (it.minWidth != newMinWidth) {
                it.minWidth = newMinWidth
                center()
            }
        }
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
