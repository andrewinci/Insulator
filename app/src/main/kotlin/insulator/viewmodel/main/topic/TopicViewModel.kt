package insulator.viewmodel.main.topic

import insulator.di.TopicScope
import insulator.di.components.TopicComponent
import insulator.helper.dispatch
import insulator.helper.runOnFXThread
import insulator.kafka.AdminApi
import insulator.kafka.model.Topic
import insulator.viewmodel.common.InsulatorViewModel
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleLongProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableStringValue
import javafx.scene.input.Clipboard
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.Window
import tornadofx.putString
import tornadofx.whenUndocked
import java.lang.Long.max
import javax.inject.Inject

@TopicScope
class TopicViewModel @Inject constructor(
    val topic: Topic,
    val adminApi: AdminApi,
    private val topicComponent: TopicComponent,
    val consumerViewModel: ConsumerViewModel
) : InsulatorViewModel() {

    private val isInternalProperty = SimpleBooleanProperty()
    private val partitionCountProperty = SimpleIntegerProperty()
    private val messageCountProperty = SimpleLongProperty()
    private val isCompactedProperty = SimpleBooleanProperty()

    val nameProperty = SimpleStringProperty(topic.name)
    val selectedItem = SimpleObjectProperty<RecordViewModel>()
    val subtitleProperty: ObservableStringValue = Bindings.createStringBinding(
        {
            val totalMessages = max(messageCountProperty.value, consumerViewModel.records.size.toLong())
            val filteredMessages = consumerViewModel.filteredRecords.value.size
            "Message count: $filteredMessages/$totalMessages - " +
                "Is internal: ${isInternalProperty.value} - " +
                "Partitions count: ${partitionCountProperty.value} - " +
                "Compacted: ${isCompactedProperty.value}"
        },
        consumerViewModel.filteredRecords.value,
        isCompactedProperty,
        partitionCountProperty,
        isInternalProperty,
        messageCountProperty
    )

    init {
        dispatch { refresh() }
    }

    suspend fun delete() = adminApi.deleteTopic(this.nameProperty.value)

    fun copySelectedRecordToClipboard() {
        if (selectedItem.value !is RecordViewModel) return
        Clipboard.getSystemClipboard().putString(selectedItem.value!!.toCsv())
    }

    fun copyAllRecordsToClipboard() {
        Clipboard.getSystemClipboard().putString(consumerViewModel.filteredRecords.value.joinToString("\n") { it.toCsv() })
    }

    private suspend fun refresh() =
        adminApi.describeTopic(topic.name).map { runOnFXThread { updateTopicProperties(it) } }

    private fun updateTopicProperties(topic: Topic) = with(topic) {
        nameProperty.set(name)
        isInternalProperty.set(isInternal ?: false)
        partitionCountProperty.set(partitionCount)
        messageCountProperty.set(messageCount ?: -1)
        isCompactedProperty.set(isCompacted)
    }

    fun showProducerView() {
        val producerView = topicComponent.getProducerView()
        Window.getWindows().map { it as Stage }.firstOrNull { it.title == producerView.title }?.toFront()
            ?: producerView.also { it.whenUndocked { dispatch { refresh() } } }
                .openWindow(modality = Modality.WINDOW_MODAL, stageStyle = StageStyle.UTILITY)
    }
}
