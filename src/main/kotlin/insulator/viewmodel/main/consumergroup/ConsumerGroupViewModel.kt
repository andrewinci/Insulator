package insulator.viewmodel.main.consumergroup

import insulator.di.getInstanceNow
import insulator.lib.helpers.completeOnFXThread
import insulator.lib.helpers.handleErrorWith
import insulator.lib.kafka.AdminApi
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.ViewModel

class ConsumerGroupViewModel(consumerGroupId: String) : ViewModel() {

    private val adminApi: AdminApi = getInstanceNow()

    val nameProperty = SimpleStringProperty(consumerGroupId)
    val state = SimpleStringProperty("...")
    internal val consumerGroupMembers: ObservableList<GroupMember> = FXCollections.observableArrayList<GroupMember>()

    init {
        adminApi.describeConsumerGroup(consumerGroupId)
            .completeOnFXThread {
                val sorted = it.members
                    .map { it.clientId to it.topicPartitions }
                    .map { (memberName, topicPartitions) ->
                        GroupMember(
                            memberName,
                            topicPartitions.groupBy { it.topic }.toList().map { (a, b) ->
                                GroupMemberTopic(a, b.sortedBy { it.partition }.map { GroupMemberTopicPartitionLag(it.partition, it.lag) })
                            }
                        )
                    }

                consumerGroupMembers.addAll(sorted)
                state.value = it.state.toString()
            }.handleErrorWith { print(it) } // todo: pass to the UI
    }

    fun resetOffset() {
        TODO("Not yet implemented")
    }
}

// Consumer groups tree
internal data class GroupMemberTopicPartitionLag(val partition: Int, val lag: Long)
internal data class GroupMemberTopic(val name: String, val partitions: List<GroupMemberTopicPartitionLag>)
internal data class GroupMember(val id: String, val topics: List<GroupMemberTopic>)
