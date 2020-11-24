package insulator.views.main.consumergroup

import insulator.ui.common.InsulatorView
import insulator.ui.component.appBar
import insulator.ui.component.refreshButton
import insulator.viewmodel.main.consumergroup.ConsumerGroupViewModel
import insulator.viewmodel.main.consumergroup.GroupMember
import insulator.viewmodel.main.consumergroup.GroupMemberTopic
import insulator.viewmodel.main.consumergroup.GroupMemberTopicPartitionLag
import javafx.beans.binding.Bindings
import javafx.event.EventTarget
import javafx.scene.control.TreeItem
import javafx.scene.layout.Priority
import tornadofx.cellFormat
import tornadofx.populate
import tornadofx.treeview
import tornadofx.vbox
import tornadofx.vgrow

class ConsumerGroupView(override val viewModel: ConsumerGroupViewModel) : InsulatorView() {

    override val root = vbox {
        appBar {
            title = viewModel.nameProperty.value
            subtitle = Bindings.createStringBinding({ "State: ${viewModel.state}" }, viewModel.state)
            buttons = listOf(refreshButton("schema", viewModel::refresh))
        }
        treeView()
    }

    private fun EventTarget.treeView() = treeview<Any> {
        root = TreeItem("Consumers")
        cellFormat {
            text = when (it) {
                is GroupMemberTopicPartitionLag -> "Partition: ${it.partition}  Lag: ${it.lag}"
                is GroupMemberTopic -> it.name
                is GroupMember -> it.id
                is String -> it
                else -> error("Invalid value type $it")
            }
        }
        populate { parent ->
            val value = parent.value
            when {
                parent == root -> viewModel.consumerGroupMembers
                value is GroupMemberTopic -> value.partitions
                value is GroupMember -> value.topics
                else -> null
            }
        }
        vgrow = Priority.ALWAYS
    }
}