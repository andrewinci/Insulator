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

class RecordViewModel(key: String, value: String) {
    val keyProperty = SimpleStringProperty(key)
    val valueProperty = SimpleStringProperty(value)
}

class TopicViewModel(topic: Topic, adminApi: AdminApi) : ViewModel() {
    private val consumer: Consumer by di()

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
    val records = FXCollections.observableArrayList<RecordViewModel>()

    fun consume() {
        if (consumer.isRunning()) return
        consumer.setCallback { k, v -> this.records.add(RecordViewModel(k, v)) }
        consumer.start(nameProperty.value)
    }

    fun stopConsumer() {
        consumer.stop()
    }
}