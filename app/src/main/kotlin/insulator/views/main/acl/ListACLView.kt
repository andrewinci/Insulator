package insulator.views.main.acl

import insulator.di.ClusterScope
import insulator.helper.dispatch
import insulator.ui.common.InsulatorView
import insulator.ui.component.appBar
import insulator.ui.component.refreshButton
import insulator.ui.component.searchBox
import insulator.viewmodel.main.acl.ListACLViewModel
import javafx.event.EventTarget
import javafx.scene.control.SelectionMode
import javafx.scene.layout.Priority
import tornadofx.bindSelected
import tornadofx.borderpane
import tornadofx.label
import tornadofx.listview
import tornadofx.onDoubleClick
import tornadofx.vbox
import tornadofx.vgrow
import javax.inject.Inject

@ClusterScope
class ListACLView @Inject constructor(
    override val viewModel: ListACLViewModel
) : InsulatorView() {

    override val root = vbox(spacing = 5.0) {
        appBar {
            title = "ACLs"
            subtitle = viewModel.subtitleProperty
            buttons = listOf(refreshButton("acl-refresh", viewModel::refresh))
        }
        borderpane {
            right = searchBox(viewModel.searchItemProperty, currentView = this@ListACLView)
        }
        ACLListView()
    }

    private fun EventTarget.ACLListView() =
        listview<String> {
            cellFormat { graphic = label(it) { id = "acl-$it" } }
            itemsProperty().set(viewModel.filteredACLProperty)
            bindSelected(viewModel.selectedACLProperty)
            onDoubleClick { dispatch { viewModel.showACL() } }

            placeholder = label("No consumer group found")
            selectionModel.selectionMode = SelectionMode.SINGLE
            vgrow = Priority.ALWAYS
        }
}
