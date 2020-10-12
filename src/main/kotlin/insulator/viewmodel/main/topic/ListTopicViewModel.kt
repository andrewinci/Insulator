package insulator.viewmodel.main.topic

import insulator.di.currentCluster
import insulator.lib.helpers.completeOnFXThread
import insulator.lib.helpers.handleErrorWith
import insulator.lib.helpers.map
import insulator.lib.kafka.AdminApi
import insulator.viewmodel.common.InsulatorViewModel
import insulator.views.common.StringScope
import insulator.views.common.customOpenWindow
import insulator.views.main.topic.CreateTopicView
import insulator.views.main.topic.TopicView
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.stage.Modality
import javafx.stage.StageStyle
import tornadofx.SortedFilteredList
import tornadofx.find
import tornadofx.whenUndockedOnce

class ListTopicViewModel : InsulatorViewModel() {

    private val adminApi: AdminApi by di()

    private val topicList: ObservableList<String> = FXCollections.observableArrayList()

    val selectedItem = SimpleStringProperty(null)
    val searchItem = SimpleStringProperty(null)
    val filteredTopics: ObservableList<String> = SortedFilteredList(topicList).apply {
        filterWhen(searchItem) { p, i -> i.toLowerCase().contains(p.toLowerCase()) }
    }.filteredItems

    init {
        refresh()
    }

    fun refresh() = adminApi
        .listTopics()
        .map { it.sorted() }
        .completeOnFXThread {
            topicList.clear()
            topicList.addAll(it)
        }
        .handleErrorWith {
            error.set(it)
        }

    fun showTopic() {
        val selectedTopicName = selectedItem.value ?: return
        StringScope("${currentCluster.guid}-$selectedTopicName")
            .withComponent(TopicViewModel(selectedTopicName))
            .let { topicView -> find<TopicView>(topicView) }
            .also { topicView -> topicView.setOnCloseListener { refresh() } }
            .let { topicView -> setMainContent(selectedTopicName, topicView) }
    }

    fun createNewTopic() =
        with(StringScope("CreateNewTopic").withComponent(CreateTopicViewModel())) {
            find<CreateTopicView>(this).also {
                it.whenUndockedOnce { refresh(); this.close() }
            }.customOpenWindow(StageStyle.UTILITY, Modality.WINDOW_MODAL)
        }
}
