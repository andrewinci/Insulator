package insulator.viewmodel

import insulator.lib.kafka.AdminApi
import insulator.lib.kafka.ConsumeFrom
import insulator.lib.kafka.Consumer
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleLongProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFxDispatcher
import kotlinx.coroutines.launch
import org.apache.avro.LogicalTypes
import tornadofx.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.temporal.TemporalAccessor


class RecordViewModel(key: String, value: String, timestamp: Long) {
    val timestamp = SimpleStringProperty(Instant.ofEpochMilli(timestamp).toString())
    val keyProperty = SimpleStringProperty(key)
    val valueProperty = SimpleStringProperty(value)
}

class TopicViewModel(private val topicName: String) : ViewModel() {
    private val CONSUME = "Consume"
    private val STOP = "Stop"
    private val adminApi: AdminApi by di()
    private val consumer: Consumer by di()


    val nameProperty = SimpleStringProperty(topicName)
    val internalProperty = SimpleBooleanProperty()
    val partitionsProperty = SimpleIntegerProperty()
    val messageCountProperty = SimpleLongProperty()
    val records: ObservableList<RecordViewModel> = FXCollections.observableArrayList<RecordViewModel>()
    val consumeButtonText = SimpleStringProperty(CONSUME)

    fun consumeButtonClick() {
        if (consumeButtonText.value == CONSUME) {
            consumeButtonText.value = STOP
            clear()
            consume(from = ConsumeFrom.Beginning)
        } else {
            consumeButtonText.value = CONSUME
            consumer.stop()
        }

    }

    fun clear() = records.clear()
    fun stop() = consumer.stop().also { consumeButtonText.value = CONSUME }

    private fun consume(from: ConsumeFrom) {
        if (consumer.isRunning()) return
        consumer.setCallback { k, v, t -> this.records.add(RecordViewModel(k, v, t)) }
        consumer.start(nameProperty.value, from)
    }

    fun loadDetails() {
        adminApi.describeTopic(topicName).unsafeRunAsync {
            it.map {
                nameProperty.set(it.first().name)
                internalProperty.set(it.first().internal ?: false)
                partitionsProperty.set(it.first().partitions ?: -1)
                messageCountProperty.set(it.first().messageCount ?: -1)
            }
        }
    }
}