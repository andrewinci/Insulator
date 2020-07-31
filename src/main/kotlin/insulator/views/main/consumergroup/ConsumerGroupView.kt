package insulator.views.main.consumergroup

import insulator.lib.kafka.ConsumeFrom
import insulator.styles.Controls
import insulator.styles.Titles
import insulator.viewmodel.main.consumergroup.ConsumerGroupViewModel
import insulator.viewmodel.main.consumergroup.GroupMember
import insulator.viewmodel.main.consumergroup.GroupMemberTopic
import insulator.viewmodel.main.consumergroup.GroupMemberTopicPartitionLag
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.geometry.Pos
import javafx.scene.control.TreeItem
import javafx.scene.layout.Priority
import tornadofx.* // ktlint-disable no-wildcard-imports
import java.util.concurrent.Callable

class ConsumerGroupView : View() {

    private val viewModel: ConsumerGroupViewModel by inject()

    private val subtitleProperty = SimpleStringProperty().also {
        it.bind(Bindings.createStringBinding(Callable { "State: ${viewModel.state.value}" }, viewModel.state))
    }

    override val root = borderpane {
        top = vbox {
            vbox {
                label(viewModel.nameProperty.value) { addClass(Titles.h1) }
                label(subtitleProperty) { addClass(Titles.h3) }
                addClass(Controls.topBarMenu, Titles.subtitle)
            }
            hbox { addClass(Controls.topBarMenuShadow) }
        }
        center = vbox(spacing = 2.0) {
            borderpane {
                left = hbox(alignment = Pos.CENTER_LEFT, spacing = 5) {
                    button("Reset offset") { action { viewModel.resetOffset() } }
                    label("to")
                    combobox<String> {
                        items = FXCollections.observableArrayList(ConsumeFrom.values().map { it.name }.toList())
                        // todo
                        // valueProperty().bindBidirectional(viewModel.consumeFromProperty)
                    }
                }
            }
            treeview<Any> {
                root = TreeItem("Consumers")
                cellFormat {
                    text = when (it) {
                        is GroupMemberTopicPartitionLag -> "Partition: ${it.partition}  Lag: ${it.lag}"
                        is GroupMemberTopic -> it.name
                        is GroupMember -> it.id
                        is String -> it
                        else -> kotlin.error("Invalid value type $it")
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
//            listview(viewModel.consumerGroupMembers) {
//                cellFormat { member ->
//                    graphic = vbox {
//                        label(member.clientId) { addClass(Titles.h2) }
//                        member.topicPartitions.map {
//                            label(" Topic: ${it.topic} \t\t Partition: ${it.partition} \t\t Lag: ${it.lag}")
//                        }
//                    }
//                }
//
//            }
        }
        addClass(Controls.view)
    }

    override fun onDock() {
        titleProperty.bind(viewModel.nameProperty)
        super.currentStage?.width = 800.0
        super.currentStage?.height = 800.0
        super.onDock()
    }
}
