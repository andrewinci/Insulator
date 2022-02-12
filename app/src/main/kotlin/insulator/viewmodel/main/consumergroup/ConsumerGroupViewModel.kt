package insulator.viewmodel.main.consumergroup

import arrow.core.computations.either
import insulator.di.ConsumerGroupId
import insulator.helper.dispatch
import insulator.helper.runOnFXThread
import insulator.kafka.AdminApi
import insulator.kafka.model.ConsumerGroupState
import insulator.viewmodel.common.InsulatorViewModel
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableStringValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javax.inject.Inject

class ConsumerGroupViewModel @Inject constructor(
    val adminApi: AdminApi,
    private val consumerGroupId: ConsumerGroupId
) : InsulatorViewModel() {

    val nameProperty = SimpleStringProperty(consumerGroupId.id)
    private val stateProperty = SimpleObjectProperty(ConsumerGroupState.UNKNOWN)
    val subtitleProperty: ObservableStringValue = Bindings.createStringBinding({ "State: ${stateProperty.value}" }, stateProperty)
    val consumerGroupMembers: ObservableList<GroupMember> = FXCollections.observableArrayList<GroupMember>()
    val canRefresh = SimpleBooleanProperty(true)

    suspend fun refresh() = either<Throwable, Unit> {
        canRefresh.set(false)
        val consumerGroup = adminApi.describeConsumerGroup(consumerGroupId.id).bind()
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
        runOnFXThread {
            consumerGroupMembers.clear()
            consumerGroupMembers.addAll(sorted)
            stateProperty.set(consumerGroup.state)
        }
    }.fold({ canRefresh.set(true); error.set(it) }, { canRefresh.set(true) })

    suspend fun delete() = adminApi.deleteConsumerGroup(nameProperty.value)
        .mapLeft { error.set(it) }

    init {
        dispatch { refresh() }
    }

    fun resetOffset() {
        TODO("Not yet implemented")
    }
}

// Consumer groups tree
data class GroupMemberTopicPartitionLag(val partition: Int, val lag: Long)
data class GroupMemberTopic(val name: String, val partitions: List<GroupMemberTopicPartitionLag>)
data class GroupMember(val id: String, val topics: List<GroupMemberTopic>)
