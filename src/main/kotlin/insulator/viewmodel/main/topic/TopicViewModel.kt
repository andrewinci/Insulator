package insulator.viewmodel.main.topic

import insulator.lib.configuration.model.Cluster
import insulator.lib.helpers.dispatch
import insulator.lib.helpers.runOnFXThread
import insulator.lib.kafka.AdminApi
import insulator.lib.kafka.ConsumeFrom
import insulator.lib.kafka.Consumer
import insulator.lib.kafka.DeserializationFormat
import insulator.ui.common.topicScope
import insulator.viewmodel.common.InsulatorViewModel
import insulator.views.main.topic.ProducerView
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

class TopicViewModel(val topicName: String) : InsulatorViewModel() {

    private val cluster: Cluster by di()
    private val adminApi: AdminApi by di()
    private val consumer: Consumer by di()

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

    val nameProperty = SimpleStringProperty(topicName)
    val isConsumingProperty = SimpleBooleanProperty(false)
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
        dispatch { refresh() }
    }

    fun clear() = records.clear()
    suspend fun stop() = consumer.stop().also { isConsumingProperty.value = false }
    suspend fun delete() = adminApi.deleteTopic(this.nameProperty.value)

    suspend fun consume() {
        if (!isConsumingProperty.value) {
            isConsumingProperty.value = true
            clear()
            consume(
                from = ConsumeFrom.valueOf(consumeFromProperty.value),
                valueFormat = DeserializationFormat.valueOf(deserializeValueProperty.value)
            )
        } else stop()
    }

    fun copySelectedRecordToClipboard() {
        if (selectedItem.value !is RecordViewModel) return
        Clipboard.getSystemClipboard().putString(selectedItem.value!!.toCsv())
    }

    fun copyAllRecordsToClipboard() {
        Clipboard.getSystemClipboard().putString(filteredRecords.value.joinToString("\n") { it.toCsv() })
    }

    private suspend fun refresh() {
        adminApi.describeTopic(topicName).map {
            runOnFXThread {
                nameProperty.set(it.name)
                isInternalProperty.set(it.isInternal ?: false)
                partitionCountProperty.set(it.partitionCount)
                messageCountProperty.set(it.messageCount ?: -1)
                isCompactedProperty.set(it.isCompacted)
            }
        }
    }

    private suspend fun consume(from: ConsumeFrom, valueFormat: DeserializationFormat) {
        if (consumer.isRunning()) return
        consumer.start(nameProperty.value, from, valueFormat) {
            val recordViewModels = it.map { (k, v, t) -> RecordViewModel(k, v, t) }
            records.runOnFXThread { addAll(recordViewModels) }
        }
    }

    fun showProduceView() = topicName.topicScope(cluster)
        .withComponent(ProducerViewModel(topicName))
        .let { find<ProducerView>(it) }
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
