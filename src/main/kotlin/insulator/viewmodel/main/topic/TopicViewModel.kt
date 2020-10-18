package insulator.viewmodel.main.topic

import insulator.di.TopicScope
import insulator.di.components.TopicComponent
import insulator.lib.helpers.completeOnFXThread
import insulator.lib.helpers.runOnFXThread
import insulator.lib.kafka.AdminApi
import insulator.lib.kafka.ConsumeFrom
import insulator.lib.kafka.Consumer
import insulator.lib.kafka.DeserializationFormat
import insulator.lib.kafka.model.Topic
import insulator.viewmodel.common.InsulatorViewModel
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleLongProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableStringValue
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.input.Clipboard
import javafx.stage.Modality
import javafx.stage.StageStyle
import tornadofx.* // ktlint-disable no-wildcard-imports
import java.util.LinkedList
import javax.inject.Inject

private const val CONSUME = "Consume"
private const val STOP = "Stop"

@TopicScope
class TopicViewModel @Inject constructor(
    val topic: Topic,
    val adminApi: AdminApi,
    val consumer: Consumer,
    val topicComponent: TopicComponent
) : InsulatorViewModel() {

    private val isInternalProperty = SimpleBooleanProperty()
    private val partitionCountProperty = SimpleIntegerProperty()
    private val messageCountProperty = SimpleLongProperty()
    private val isCompactedProperty = SimpleBooleanProperty()

    val records: ObservableList<RecordViewModel> = FXCollections.observableList(LinkedList())
    val filteredRecords = SimpleObjectProperty<ObservableList<RecordViewModel>>()
        .also { prop ->
            prop.onChange { list ->
                list?.onChange {
                    messageConsumedCountProperty.value = list.size
                }
            }
        }
    private val messageConsumedCountProperty = SimpleIntegerProperty()

    val nameProperty = SimpleStringProperty(topic.name)
    val consumeButtonText = SimpleStringProperty(CONSUME)
    val consumeFromProperty = SimpleStringProperty(ConsumeFrom.LastDay.toString())
    val deserializeValueProperty = SimpleStringProperty(DeserializationFormat.String.toString())
    val selectedItem = SimpleObjectProperty<RecordViewModel>()
    val searchItem = SimpleStringProperty()
    val subtitleProperty: ObservableStringValue = Bindings.createStringBinding(
        {
            "Message count: ${messageConsumedCountProperty.value}/${messageCountProperty.value} - " +
                "Is internal: ${isInternalProperty.value} - " +
                "Partitions count: ${partitionCountProperty.value} - " +
                "Compacted: ${isCompactedProperty.value}"
        },
        messageConsumedCountProperty,
        isCompactedProperty,
        partitionCountProperty,
        isInternalProperty,
        messageCountProperty
    )

    init {
        updateTopicProperties(topic)
    }

    fun clear() = records.clear()
    fun stop() = consumer.stop().also { consumeButtonText.value = CONSUME }
    fun delete() {
        adminApi.deleteTopic(this.nameProperty.value).get()
    }

    fun consume() {
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

    fun copySelectedRecordToClipboard() {
        if (selectedItem.value !is RecordViewModel) return
        Clipboard.getSystemClipboard().putString(selectedItem.value!!.toCsv())
    }

    fun copyAllRecordsToClipboard() {
        Clipboard.getSystemClipboard().putString(filteredRecords.value.joinToString("\n") { it.toCsv() })
    }

    private fun refresh() =
        adminApi.describeTopic(topic.name).completeOnFXThread { updateTopicProperties(it) }

    private fun updateTopicProperties(topic: Topic) = with(topic) {
        nameProperty.set(name)
        isInternalProperty.set(isInternal ?: false)
        partitionCountProperty.set(partitionCount)
        messageCountProperty.set(messageCount ?: -1)
        isCompactedProperty.set(isCompacted)
    }

    private fun consume(from: ConsumeFrom, valueFormat: DeserializationFormat) {
        if (consumer.isRunning()) return
        consumer.start(nameProperty.value, from, valueFormat) {
            val recordViewModels = it.map { (k, v, t) -> RecordViewModel(k, v, t) }
            records.runOnFXThread { addAll(recordViewModels) }
        }
    }

    fun showProduceView() = topicComponent
        .getProducerView()
        .openWindow(modality = Modality.WINDOW_MODAL, stageStyle = StageStyle.UTILITY)

    fun configureFilteredRecords(comparator: ObservableValue<Comparator<RecordViewModel>>) {
        filteredRecords.set(
            SortedFilteredList(records).apply {
                filterWhen(searchItem) { p, i ->
                    i.keyProperty.value?.toLowerCase()?.contains(p.toLowerCase()) ?: false ||
                        i.valueProperty.value.toLowerCase().contains(p.toLowerCase())
                }
            }.sortedItems.also {
                it.comparatorProperty().bind(comparator)
            }
        )
    }
}
