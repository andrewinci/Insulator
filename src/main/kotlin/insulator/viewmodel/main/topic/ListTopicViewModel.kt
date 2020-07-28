package insulator.viewmodel.main.topic

import insulator.lib.helpers.map
import insulator.lib.kafka.AdminApi
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.ViewModel

class ListTopicViewModel : ViewModel() {
    private val adminApi: AdminApi by di()

    val topicList: ObservableList<String> by lazy {
        FXCollections.observableArrayList<String>().also { collection ->
            adminApi.listTopics()
                .map { it.sorted() }
                .map { collection.addAll(it) }
            // todo: handle exception
        }
    }
}
