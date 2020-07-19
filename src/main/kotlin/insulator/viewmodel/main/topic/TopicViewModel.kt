package insulator.viewmodel.main.topic

import insulator.di.getInstanceNow
import insulator.lib.kafka.AdminApi
import insulator.lib.kafka.ConsumeFrom
import insulator.lib.kafka.Consumer
import insulator.lib.kafka.DeserializationFormat
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleLongProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.*

private const val CONSUME = "Consume"
private const val STOP = "Stop"

class TopicViewModel(private val topicName: String) : ViewModel() {

    private val adminApi: AdminApi = getInstanceNow()
    private val consumer: Consumer = getInstanceNow()

    val nameProperty = SimpleStringProperty(topicName)
    val internalProperty = SimpleBooleanProperty()
    val partitionsProperty = SimpleIntegerProperty()
    val messageCountProperty = SimpleLongProperty()
    val records: ObservableList<RecordViewModel> = FXCollections.observableArrayList<RecordViewModel>()
    val consumeButtonText = SimpleStringProperty(CONSUME)
    val consumeFromProperty = SimpleStringProperty(ConsumeFrom.Beginning.name)
    
    val deserializeValueProperty = SimpleStringProperty(DeserializationFormat.String.name)

    fun consumeButtonClick() {
        if (consumeButtonText.value == CONSUME) {
            consumeButtonText.value = STOP
            clear()
            consume(
                    from = ConsumeFrom.valueOf(consumeFromProperty.value),
                    valueFormat = DeserializationFormat.valueOf(deserializeValueProperty.value))
        } else {
            consumeButtonText.value = CONSUME
            consumer.stop()
        }

    }

    fun clear() = records.clear()
    fun stop() = consumer.stop().also { consumeButtonText.value = CONSUME }

    private fun consume(from: ConsumeFrom, valueFormat: DeserializationFormat) {
        if (consumer.isRunning()) return
        consumer.start(nameProperty.value, from, valueFormat) { k, v, t -> this.records.add(RecordViewModel(k, v, t)) }
    }

    fun loadDetails() {
        adminApi.describeTopic(topicName).unsafeRunAsync { topic ->
            topic.map {
                nameProperty.set(it.first().name)
                internalProperty.set(it.first().internal ?: false)
                partitionsProperty.set(it.first().partitions ?: -1)
                messageCountProperty.set(it.first().messageCount ?: -1)
            }
        }
    }
}