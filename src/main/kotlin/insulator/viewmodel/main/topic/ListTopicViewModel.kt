package insulator.viewmodel.main.topic

import insulator.lib.kafka.AdminApi
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.ViewModel

class ListTopicViewModel : ViewModel() {
    private val adminApi: AdminApi by di()

    fun listTopics(): ObservableList<String> =
        adminApi.listTopics()
            .unsafeRunSync()
            .sortedBy { it }
            .let { FXCollections.observableList(it) }
}
