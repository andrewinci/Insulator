package insulator.viewmodel.main.consumergroup

import arrow.core.computations.either
import insulator.helper.dispatch
import insulator.kafka.AdminApi
import insulator.viewmodel.common.InsulatorViewModel
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javax.inject.Inject


class ConsumerGroupViewModel @Inject constructor(val adminApi: AdminApi) : InsulatorViewModel() {
    private val consumerGroupId = "test" //todo: need to pass the consumer groupId

    val nameProperty = SimpleStringProperty(consumerGroupId)
    val state = SimpleStringProperty("...")
    val consumerGroupMembers: ObservableList<GroupMember> = FXCollections.observableArrayList<GroupMember>()

    init {
        dispatch { refresh() }
    }

    suspend fun refresh() = either<Throwable, Unit> {
        val consumerGroup = !adminApi.describeConsumerGroup(consumerGroupId)
        val sorted = consumerGroup.members
            .map { it.clientId to it.topicPartitions }
            .map { (memberName, topicPartitions) ->
                GroupMember(
                    memberName,
                    topicPartitions.groupBy { it.topic }.toList().map { (a, b) ->
                        GroupMemberTopic(a, b.sortedBy { it.partition }.map { GroupMemberTopicPartitionLag(it.partition, it.lag) })
                    }
                )
            }
        consumerGroupMembers.clear()
        consumerGroupMembers.addAll(sorted)
        state.value = consumerGroup.state.toString()
    }.mapLeft { error.set(it) }


    fun resetOffset() {
        TODO("Not yet implemented")
    }
}

// Consumer groups tree
data class GroupMemberTopicPartitionLag(val partition: Int, val lag: Long)
data class GroupMemberTopic(val name: String, val partitions: List<GroupMemberTopicPartitionLag>)
data class GroupMember(val id: String, val topics: List<GroupMemberTopic>)