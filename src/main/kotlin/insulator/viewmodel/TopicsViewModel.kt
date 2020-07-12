package insulator.viewmodel

import insulator.kafka.AdminApi
import insulator.model.Topic
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import tornadofx.*

class TopicsViewModel(private val adminApi: AdminApi) : ViewModel() {
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

class TopicViewModel(topic: Topic, adminApi: AdminApi) {
    init {
        GlobalScope.launch {
            adminApi.describeTopic(topic.name).unsafeRunAsync {
                it.map {
                    nameProperty.set(it.first().name)
                    internalProperty.set(it.first().internal ?: false)
                    partitionsProperty.set(it.first().partitions ?: -1)
                }
            }
        }
    }

    val nameProperty = SimpleStringProperty(topic.name)
    val messageCountProperty = SimpleIntegerProperty()
    val internalProperty = SimpleBooleanProperty()
    val partitionsProperty = SimpleIntegerProperty()
}