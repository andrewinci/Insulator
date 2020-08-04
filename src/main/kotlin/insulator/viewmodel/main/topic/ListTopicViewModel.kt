package insulator.viewmodel.main.topic

import insulator.lib.helpers.map
import insulator.lib.kafka.AdminApi
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.ViewModel

class ListTopicViewModel : ViewModel() {

    private val adminApi: AdminApi by di()

    val topicList: ObservableList<String> = FXCollections.observableArrayList<String>()

    init {
        refresh()
    }

    fun refresh() {
        adminApi.listTopics()
            .map { it.sorted() }
            .map { Platform.runLater { topicList.clear(); topicList.addAll(it) } }
        // todo: handle exception
    }
}
