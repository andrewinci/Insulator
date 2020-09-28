package insulator.viewmodel.main.topic

import insulator.di.getInstanceNow
import insulator.lib.configuration.model.Cluster
import insulator.lib.helpers.completeOnFXThread
import insulator.lib.helpers.runOnFXThread
import insulator.lib.kafka.AdminApi
import insulator.lib.kafka.ConsumeFrom
import insulator.lib.kafka.Consumer
import insulator.lib.kafka.DeserializationFormat
import insulator.viewmodel.common.InsulatorViewModel
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleLongProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.input.Clipboard
import tornadofx.* // ktlint-disable no-wildcard-imports
import java.util.LinkedList

private const val CONSUME = "Consume"
private const val STOP = "Stop"

class TopicViewModel(val topicName: String) : InsulatorViewModel() {

    private val adminApi: AdminApi = getInstanceNow()
    private val consumer: Consumer = getInstanceNow()
    val cluster: Cluster by di()

    private val isInternalProperty = SimpleBooleanProperty()
    private val partitionCountProperty = SimpleIntegerProperty()
    private val messageCountProperty = SimpleLongProperty()
    private val isCompactedProperty = SimpleBooleanProperty()

    val records: ObservableList<RecordViewModel> = FXCollections.observableList(LinkedList<RecordViewModel>())
    val filteredRecords = SimpleObjectProperty<ObservableList<RecordViewModel>>()
        .also { prop ->
            prop.onChange { list ->
                list?.onChange {
                    messageConsumedCountProperty.value = list.size ?: 0
                }
            }
        }
    private val messageConsumedCountProperty = SimpleIntegerProperty()

    val nameProperty = SimpleStringProperty(topicName)
    val consumeButtonText = SimpleStringProperty(CONSUME)
    val consumeFromProperty = SimpleStringProperty(ConsumeFrom.LastDay.toString())
    val deserializeValueProperty = SimpleStringProperty(DeserializationFormat.String.toString())
    val selectedItem = SimpleObjectProperty<RecordViewModel>()
    val searchItem = SimpleStringProperty()
    val subtitleProperty: ObservableValue<String> = Bindings.createStringBinding(
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

    init { refresh() }

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

    private fun refresh() {
        adminApi.describeTopic(topicName).completeOnFXThread {
            nameProperty.set(it.name)
            isInternalProperty.set(it.isInternal ?: false)
            partitionCountProperty.set(it.partitionCount)
            messageCountProperty.set(it.messageCount ?: -1)
            isCompactedProperty.set(it.isCompacted)
        }
    }

    private fun consume(from: ConsumeFrom, valueFormat: DeserializationFormat) {
        if (consumer.isRunning()) return
        consumer.start(nameProperty.value, from, valueFormat) {
            val recordViewModels = it.map { (k, v, t) -> RecordViewModel(k, v, t) }
            records.runOnFXThread { addAll(recordViewModels) }
        }
    }
}
