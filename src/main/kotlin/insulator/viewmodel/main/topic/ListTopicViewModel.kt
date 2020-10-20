package insulator.viewmodel.main.topic

import insulator.di.factories.TopicComponentFactory
import insulator.lib.configuration.model.Cluster
import insulator.lib.helpers.dispatch
import insulator.lib.helpers.runOnFXThread
import insulator.lib.kafka.AdminApi
import insulator.lib.kafka.model.Topic
import insulator.viewmodel.common.InsulatorViewModel
import insulator.viewmodel.main.TabViewModel
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableStringValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.stage.Modality
import javafx.stage.StageStyle
import tornadofx.SortedFilteredList
import tornadofx.whenUndockedOnce
import javax.inject.Inject

class ListTopicViewModel @Inject constructor(
    val cluster: Cluster,
    val adminApi: AdminApi,
    private val viewFactory: TopicComponentFactory,
    val tabViewModel: TabViewModel
) : InsulatorViewModel() {

    private val topicListProperty: ObservableList<String> = FXCollections.observableArrayList()

    val selectedItemProperty = SimpleStringProperty(null)
    val searchItemProperty = SimpleStringProperty(null)
    val filteredTopicsProperty: ObservableList<String> = SortedFilteredList(topicListProperty)
        .apply { filterWhen(searchItemProperty) { p, i -> i.toLowerCase().contains(p.toLowerCase()) } }
        .filteredItems
    val subtitleProperty: ObservableStringValue = Bindings.createStringBinding(
        { "Topic count: ${filteredTopicsProperty.size}/${topicListProperty.size}" },
        topicListProperty,
        filteredTopicsProperty
    )

    init {
        dispatch { refresh() }
    }

    suspend fun refresh() = adminApi
        .listTopics()
        .map { it.sorted() }
        .fold(
            {
                error.set(it)
            },
            {
                runOnFXThread {
                    topicListProperty.clear()
                    topicListProperty.addAll(it)
                }
            }
        )

    suspend fun showTopic() {
        val selectedTopicName = selectedItemProperty.value ?: return
        adminApi.describeTopic(selectedTopicName)
            .map {
                viewFactory
                    .build(it)
                    .getTopicView()
                    .also { topicView -> topicView.setOnCloseListener { dispatch { refresh() } } }
                    .let { topicView -> tabViewModel.setMainContent(selectedTopicName, topicView) }
            }
    }

    fun createNewTopic() = viewFactory.build(Topic.empty())
        .getCreateTopicView()
        .also { it.whenUndockedOnce { dispatch { refresh() } } }
        .openWindow(StageStyle.UTILITY, Modality.WINDOW_MODAL)
}
