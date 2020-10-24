package insulator.viewmodel.main

import insulator.di.ClusterScope
import insulator.helper.runOnFXThread
import insulator.ui.common.InsulatorTabView
import javafx.collections.ObservableList
import javafx.scene.control.Tab
import tornadofx.close
import tornadofx.select
import javax.inject.Inject

@ClusterScope
class TabViewModel @Inject constructor() {
    lateinit var contentTabs: ObservableList<Tab>

    fun setMainContent(title: String, view: InsulatorTabView): Unit =
        runOnFXThread { showTab(title, view) }

    fun showTab(title: String, view: InsulatorTabView) {
        val existingTab = contentTabs.firstOrNull { it.content == view.root }
        val newTab = Tab(title, view.root)
            .also { it.setOnClosed { view.onTabClosed() } }
            .also { view.setOnCloseListener { it.close() } }

        existingTab?.select() ?: with(newTab) {
            contentTabs.add(this).also { this.select() }
        }
    }
}
