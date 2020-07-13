package insulator.viewmodel

import insulator.kafka.AdminApi
import insulator.kafka.Consumer
import insulator.model.Topic
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import tornadofx.*

class ListTopicViewModel(private val adminApi: AdminApi) : ViewModel() {
    private var updating = false;
    private val internal = FXCollections.observableArrayList<TopicViewModel>()

    val topicsProperty: ObservableList<TopicViewModel> by lazy {
        update()
        internal
    }

    private fun update() {
        if (updating) return
        updating = true
        GlobalScope.launch {
            adminApi.listTopics()
                    .unsafeRunAsync {
                        it.map { topics -> internal.addAll(topics.map { TopicViewModel(it, adminApi) }) }
                    }
        }
    }
}