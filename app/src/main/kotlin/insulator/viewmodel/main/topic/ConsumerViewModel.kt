package insulator.viewmodel.main.topic

import insulator.di.TopicScope
import insulator.helper.runOnFXThread
import insulator.kafka.consumer.ConsumeFrom
import insulator.kafka.consumer.Consumer
import insulator.kafka.model.Topic
import insulator.kafka.producer.SerializationFormat
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.SortedFilteredList
import java.util.LinkedList
import javax.inject.Inject

@TopicScope
class ConsumerViewModel @Inject constructor(
    val topic: Topic,
    private val consumer: Consumer
) {

    val records: ObservableList<RecordViewModel> = FXCollections.observableList(LinkedList())
    val isConsumingProperty = SimpleBooleanProperty(false)
    val consumeFromProperty = SimpleStringProperty(ConsumeFrom.LastDay.text)
    val deserializeValueProperty = SimpleStringProperty(SerializationFormat.String.toString())
    val searchItem = SimpleStringProperty("")
    val comparatorProperty = SimpleObjectProperty<Comparator<RecordViewModel>>()

    val filteredRecords = SimpleObjectProperty<ObservableList<RecordViewModel>>(
        SortedFilteredList(records).apply {
            filterWhen(searchItem) { p, i ->
                i.keyProperty.value?.toLowerCase()?.contains(p.toLowerCase()) ?: false ||
                    i.valueProperty.value.toLowerCase().contains(p.toLowerCase())
            }
        }.sortedItems.also {
            it.comparatorProperty().bind(comparatorProperty)
        }
    )

    fun clearRecords() = records.clear()

    suspend fun stop() = consumer.stop().also { isConsumingProperty.value = false }

    suspend fun consume() {
        if (!isConsumingProperty.value) {
            isConsumingProperty.value = true
            clearRecords()
            val consumerFrom = ConsumeFrom.values().first { it.text == consumeFromProperty.value }
            val deserializationFormat = SerializationFormat.valueOf(deserializeValueProperty.value)
            consumer.start(topic.name, consumerFrom, deserializationFormat) {
                records.runOnFXThread { addAll(it.map { record -> RecordViewModel(record) }) }
            }
        } else stop()
    }
}
