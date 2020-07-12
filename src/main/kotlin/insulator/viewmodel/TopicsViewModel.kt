package insulator.viewmodel

import arrow.fx.IO
import insulator.kafka.AdminApi
import insulator.model.Topic
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.*

class TopicsViewModel(val adminApi: AdminApi) : ViewModel() {
    fun getTopics(): IO<ObservableList<Topic>> =
        adminApi.listTopics().map { FXCollections.observableArrayList(it) }

}