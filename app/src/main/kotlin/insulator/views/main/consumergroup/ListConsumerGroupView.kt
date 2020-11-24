package insulator.views.main.consumergroup

import insulator.di.ClusterScope
import insulator.helper.dispatch
import insulator.ui.common.InsulatorView
import insulator.ui.component.appBar
import insulator.ui.component.refreshButton
import insulator.ui.component.searchBox
import insulator.viewmodel.main.consumergroup.ListConsumerGroupViewModel
import insulator.viewmodel.main.schemaregistry.LoadSchemaListError
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
class ListConsumerGroupView @Inject constructor(
    override val viewModel: ListConsumerGroupViewModel
) : InsulatorView() {

    override val root = vbox(spacing = 5.0) {
        appBar {
            title = "Consumer groups"
            subtitle = viewModel.subtitleProperty
            buttons = listOf(refreshButton("consumer-groups-refresh", viewModel::refresh))
        }
        borderpane {
            right = searchBox(viewModel.searchItemProperty, currentView = this@ListConsumerGroupView)
        }
        schemasListView()
    }

    private fun EventTarget.schemasListView() =
        listview<String> {
            cellFormat { graphic = label(it) { id = "consumer-$it" } }
            itemsProperty().set(viewModel.filteredConsumerGroupsProperty)
            bindSelected(viewModel.selectedConsumerGroupProperty)
            onDoubleClick { dispatch { viewModel.showConsumerGroup() } }
            placeholder = label("No schema found")
            selectionModel.selectionMode = SelectionMode.SINGLE
            vgrow = Priority.ALWAYS
        }

    override fun onError(throwable: Throwable) {
        when (throwable) {
            is LoadSchemaListError -> return
            else -> viewModel.dispatch { refresh() }
        }
    }
}
