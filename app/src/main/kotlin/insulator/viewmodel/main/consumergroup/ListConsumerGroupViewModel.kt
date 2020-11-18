package insulator.viewmodel.main.consumergroup

import insulator.di.ClusterScope
import insulator.helper.dispatch
import insulator.helper.runOnFXThread
import insulator.kafka.AdminApi
import insulator.viewmodel.common.InsulatorViewModel
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableStringValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.SortedFilteredList
import javax.inject.Inject

@ClusterScope
class ListConsumerGroupViewModel @Inject constructor(
    private val adminClient: AdminApi,
) : InsulatorViewModel() {

    private val consumerGroupsProperty: ObservableList<String> = FXCollections.observableArrayList()

    val selectedConsumerGroupProperty = SimpleStringProperty()
    val searchItemProperty = SimpleStringProperty()

    val filteredConsumerGroupsProperty = SortedFilteredList(consumerGroupsProperty).apply {
        filterWhen(searchItemProperty) { p, i -> i.toLowerCase().contains(p.toLowerCase()) }
    }.filteredItems

    val subtitleProperty: ObservableStringValue = Bindings.createStringBinding(
        {
            "Consumer groups count: ${filteredConsumerGroupsProperty.size}/${consumerGroupsProperty.size}"
        },
        consumerGroupsProperty,
        filteredConsumerGroupsProperty
    )

    init {
        dispatch { refresh() }
    }

    suspend fun refresh() = adminClient
        .listConsumerGroups()
        .map {
            it.runOnFXThread {
                consumerGroupsProperty.clear()
                consumerGroupsProperty.addAll(it)
            }
        }.mapLeft {
            error.set(LoadConsumerGroupError(it.message ?: "Unable to load the consumer group list"))
        }
}

class LoadConsumerGroupError(message: String) : Error(message)
