package insulator.viewmodel

import insulator.lib.kafka.AdminApi
import insulator.lib.kafka.ConsumeFrom
import insulator.lib.kafka.Consumer
import insulator.lib.kafka.model.Topic
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleLongProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import tornadofx.*


class RecordViewModel(key: String, value: String, timestamp: Long) {
    val timestamp = SimpleLongProperty(timestamp)
    val keyProperty = SimpleStringProperty(key)
    val valueProperty = SimpleStringProperty(value)
}

class TopicViewModel(private val topicName: String) : ViewModel() {

    private val adminApi: AdminApi by di()
    private val consumer: Consumer by di()

    init {
        GlobalScope.launch {
            adminApi.describeTopic(topicName).unsafeRunAsync {
                it.map {
                    nameProperty.set(it.first().name)
                    internalProperty.set(it.first().internal ?: false)
                    partitionsProperty.set(it.first().partitions ?: -1)
                }
            }
        }
    }

    val nameProperty = SimpleStringProperty(topicName)
    val internalProperty = SimpleBooleanProperty()
    val partitionsProperty = SimpleIntegerProperty()

    val records : ObservableList<RecordViewModel> = FXCollections.observableArrayList<RecordViewModel>()
    val consumeButtonText = SimpleStringProperty("Consume")

    fun consumeButtonClick() {
        if (consumeButtonText.value == "Consume") {
            consumeButtonText.value = "Stop"
            clear()
            consume(from = ConsumeFrom.Beginning)
        } else {
            consumeButtonText.value = "Consume"
            consumer.stop()
        }

    }

    fun clear() = records.clear()
    fun stop() = consumer.stop()

    private fun consume(from: ConsumeFrom) {
        if (consumer.isRunning()) return
        consumer.setCallback { k, v, t -> this.records.add(RecordViewModel(k, v, t)) }
        consumer.start(nameProperty.value, from)
    }

    fun getMessageCount(): Long? = adminApi.getApproximateMessageCount(topicName).unsafeRunSync()
}