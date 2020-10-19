package insulator.viewmodel.main.topic

import insulator.lib.configuration.model.Cluster
import insulator.lib.helpers.dispatch
import insulator.lib.helpers.runOnFXThread
import insulator.lib.kafka.AdminApi
import insulator.ui.common.topicScope
import insulator.viewmodel.common.InsulatorViewModel
import insulator.views.main.topic.CreateTopicView
import insulator.views.main.topic.TopicView
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableStringValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.stage.Modality
import javafx.stage.StageStyle
import tornadofx.SortedFilteredList
import tornadofx.find
import tornadofx.whenUndockedOnce

class ListTopicViewModel : InsulatorViewModel() {

    private val cluster: Cluster by di()
    private val adminApi: AdminApi by di()
    private val topicListProperty: ObservableList<String> = FXCollections.observableArrayList()

    val selectedItemProperty = SimpleStringProperty(null)
    val searchItemProperty = SimpleStringProperty(null)
    val filteredTopicsProperty: ObservableList<String> = SortedFilteredList(topicListProperty).apply {
        filterWhen(searchItemProperty) { p, i -> i.toLowerCase().contains(p.toLowerCase()) }
    }.filteredItems
    val subtitleProperty: ObservableStringValue = Bindings.createStringBinding(
        {
            "Topic count: ${filteredTopicsProperty.size}/${topicListProperty.size}"
        },
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

    fun showTopic() =
        selectedItemProperty.value?.let {
            it.topicScope(cluster)
                .withComponent(TopicViewModel(selectedItemProperty.value))
                .let { topicView -> find<TopicView>(topicView) }
                .also { topicView -> topicView.setOnCloseListener { dispatch { refresh() } } }
                .also { topicView -> setMainContent(selectedItemProperty.value, topicView) }
        }

    fun createNewTopic() = "new-topic".topicScope(cluster)
        .withComponent(CreateTopicViewModel())
        .let { scope -> find<CreateTopicView>(scope).also { it.whenUndockedOnce { dispatch { refresh(); scope.close() } } } }
        .openWindow(StageStyle.UTILITY, Modality.WINDOW_MODAL)
}