package insulator.viewmodel.main.topic

import insulator.di.getInstanceNow
import insulator.lib.helpers.map
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
import tornadofx.ViewModel

private const val CONSUME = "Consume"
private const val STOP = "Stop"

class TopicViewModel(topicName: String) : ViewModel() {

    private val adminApi: AdminApi = getInstanceNow()
    private val consumer: Consumer = getInstanceNow()

    val nameProperty = SimpleStringProperty(topicName)
    val isInternalProperty = SimpleBooleanProperty()
    val partitionCountProperty = SimpleIntegerProperty()
    val messageCountProperty = SimpleLongProperty()

    val records: ObservableList<RecordViewModel> = FXCollections.observableArrayList<RecordViewModel>()

    val consumeButtonText = SimpleStringProperty(CONSUME)
    val consumeFromProperty = SimpleStringProperty(ConsumeFrom.LastDay.name)
    val deserializeValueProperty = SimpleStringProperty(DeserializationFormat.Avro.name)

    init {
        adminApi.describeTopic(topicName).get().map {
            nameProperty.set(it.first().name)
            isInternalProperty.set(it.first().isInternal ?: false)
            partitionCountProperty.set(it.first().partitionCount ?: -1)
            messageCountProperty.set(it.first().messageCount ?: -1)
        }
    }

    fun consumeButtonClick() {
        if (consumeButtonText.value == CONSUME) {
            consumeButtonText.value = STOP
            clear()
            consume(
                from = ConsumeFrom.valueOf(consumeFromProperty.value),
                valueFormat = DeserializationFormat.valueOf(deserializeValueProperty.value)
            )
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
}
