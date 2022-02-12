package insulator.viewmodel.main.topic

import insulator.di.ClusterScope
import insulator.di.TopicComponentFactory
import insulator.helper.dispatch
import insulator.helper.runOnFXThread
import insulator.kafka.AdminApi
import insulator.kafka.model.Cluster
import insulator.kafka.model.Topic
import insulator.ui.WindowsManager
import insulator.viewmodel.common.InsulatorViewModel
import insulator.viewmodel.main.TabViewModel
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableStringValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.stage.Window
import tornadofx.SortedFilteredList
import tornadofx.whenUndockedOnce
import javax.inject.Inject

@ClusterScope
class ListTopicViewModel @Inject constructor(
    val cluster: Cluster,
    val adminApi: AdminApi,
    private val topicComponentFactory: TopicComponentFactory,
    val tabViewModel: TabViewModel,
    val windowsManager: WindowsManager
) : InsulatorViewModel() {

    private val topicListProperty: ObservableList<String> = FXCollections.observableArrayList()

    val selectedItemProperty = SimpleStringProperty(null)
    val searchItemProperty = SimpleStringProperty(null)
    val filteredTopicsProperty: ObservableList<String> = SortedFilteredList(topicListProperty)
        .apply { filterWhen(searchItemProperty) { p, i -> i.lowercase().contains(p.lowercase()) } }
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
        topicComponentFactory
            .build(Topic(selectedTopicName))
            .getTopicView()
            .also { topicView -> topicView.setOnCloseListener { dispatch { refresh() } } }
            .let { topicView -> tabViewModel.setMainContent(selectedTopicName, topicView) }
    }

    fun createNewTopic(owner: Window?) = windowsManager.openWindow("create-new-topic", owner) {
        topicComponentFactory.build(Topic.empty())
            .getCreateTopicView()
            .also { it.whenUndockedOnce { dispatch { refresh() } } }
    }
}
