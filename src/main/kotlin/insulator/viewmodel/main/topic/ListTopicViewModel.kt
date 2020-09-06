package insulator.viewmodel.main.topic

import insulator.lib.helpers.handleErrorWith
import insulator.lib.helpers.map
import insulator.lib.helpers.runOnFXThread
import insulator.lib.kafka.AdminApi
import insulator.viewmodel.common.InsulatorViewModel
import javafx.collections.FXCollections
import javafx.collections.ObservableList

class ListTopicViewModel : InsulatorViewModel() {

    private val adminApi: AdminApi by di()

    val topicList: ObservableList<String> = FXCollections.observableArrayList<String>()

    init { refresh() }

    fun refresh() = adminApi
        .listTopics()
        .map { it.sorted() }
        .runOnFXThread {
            topicList.clear()
            topicList.addAll(it)
        }
        .handleErrorWith {
            error.set(it)
        }
}
