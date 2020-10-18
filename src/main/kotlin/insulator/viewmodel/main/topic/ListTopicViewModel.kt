package insulator.viewmodel.main.topic

import insulator.di.components.TopicComponent
import insulator.di.factories.Factory
import insulator.lib.configuration.model.Cluster
import insulator.lib.helpers.completeOnFXThread
import insulator.lib.helpers.handleErrorWith
import insulator.lib.helpers.map
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
    private val viewFactory: Factory<Topic, TopicComponent>,
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
        refresh()
    }

    fun refresh() = adminApi
        .listTopics()
        .map { it.sorted() }
        .completeOnFXThread {
            topicListProperty.clear()
            topicListProperty.addAll(it)
        }
        .handleErrorWith {
            error.set(it)
        }

    fun showTopic() {
        val selectedTopicName = selectedItemProperty.value ?: return
        adminApi.describeTopic(selectedTopicName)
            .map {
                viewFactory
                    .build(it)
                    .getTopicView()
                    .also { topicView -> topicView.setOnCloseListener { refresh() } }
                    .let { topicView -> tabViewModel.setMainContent(selectedTopicName, topicView) }
            }

//        selectedItemProperty.value.topicScope(cluster)
//            .withComponent(TopicViewModel(selectedTopicName))
//            .let { topicView -> find<TopicView>(topicView) }
//            .also { topicView -> topicView.setOnCloseListener { refresh() } }
//            .let { topicView -> setMainContent(selectedTopicName, topicView) }
    }

    fun createNewTopic() = viewFactory.build(Topic.empty())
        .getCreateTopicView()
        .also { it.whenUndockedOnce { refresh() } }
        .openWindow(StageStyle.UTILITY, Modality.WINDOW_MODAL)
}
