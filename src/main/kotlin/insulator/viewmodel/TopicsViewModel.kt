package insulator.viewmodel

import arrow.core.Either
import arrow.core.right
import insulator.kafka.AdminApi
import insulator.model.Topic
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.*

class TopicsViewModel(val adminApi: AdminApi) : ViewModel() {
    val topics: Either<Throwable, ObservableList<Topic>> by lazy {
        adminApi.listTopics().map { FXCollections.observableArrayList(it) }
    }
}