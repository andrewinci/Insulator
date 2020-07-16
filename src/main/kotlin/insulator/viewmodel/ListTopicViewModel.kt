package insulator.viewmodel

import insulator.lib.kafka.AdminApi
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.*

class ListTopicViewModel : ViewModel() {
    private val adminApi: AdminApi by di()

    fun listTopics(): ObservableList<TopicViewModel> =
            adminApi.listTopics()
                    .map { it.map { topic -> TopicViewModel(topic) } }
                    .unsafeRunSync()
                    .sortedBy { it.nameProperty.value }
                    .let { FXCollections.observableArrayList(it)}
}