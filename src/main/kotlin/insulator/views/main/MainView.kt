package insulator.views.main

import insulator.di.currentCluster
import insulator.viewmodel.main.MainViewModel
import insulator.views.common.InsulatorView
import insulator.views.component.h1
import insulator.views.component.h2
import insulator.views.component.themeButton
import insulator.views.main.schemaregistry.ListSchemaView
import insulator.views.main.topic.ListTopicView
import insulator.views.style.MainViewStyle
import insulator.views.style.changeTheme
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

class MainView : InsulatorView<MainViewModel>("Insulator", MainViewModel::class) {
    private val sidebarWidth = 250.0
    private val contentListWidth = 450.0
    private val contentWidth = 780.0

    private val contentList = borderpane {
        centerProperty().bind(viewModel.contentList)
        addClass(MainViewStyle.contentList)
        minWidth = contentListWidth
        maxWidth = contentListWidth
    }

    private val content = tabpane {
        viewModel.contentTabs = tabs
        minWidth = contentWidth
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
                viewModel.contentTabs.size == 0 -> nodes.removeAt(2)
                nodes.size <= 2 -> nodes.add(content)
                else -> nodes[2] = content
            }
        }
        nodes.onChange { setSize() }
    }

    private fun sidebar() =
        borderpane {
            top = vbox(alignment = Pos.TOP_CENTER) {
                h1(currentCluster.name)
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
            minWidth = sidebarWidth
            maxWidth = sidebarWidth
        }

    private fun EventTarget.menuItem(name: String, icon: String, onClick: () -> Unit) =
        hbox(spacing = 5.0) {
            imageview(Image(icon)) { fitHeight = 35.0; fitWidth = 35.0; }
            h2(name)
            onMouseClicked = EventHandler { onClick() }
            addClass(MainViewStyle.sidebarItem)
        }

    private fun setSize() {
        val min = sidebarWidth + contentListWidth
        if (nodes.size == 2) {
            super.currentStage?.maxWidth = min
            super.currentStage?.minWidth = min
        } else {
            super.currentStage?.maxWidth = Double.MAX_VALUE
            super.currentStage?.minWidth = min + contentWidth
        }
    }

    override fun onDock() {
        super.onDock()
        setSize()
        super.currentStage?.height = 800.0
        super.currentStage?.isResizable = true
    }

    override fun onError(throwable: Throwable) {
        close()
    }
}
