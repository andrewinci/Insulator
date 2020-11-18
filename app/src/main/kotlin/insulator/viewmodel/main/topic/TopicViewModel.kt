package insulator.viewmodel.main.topic

import insulator.di.TopicScope
import insulator.di.components.TopicComponent
import insulator.helper.dispatch
import insulator.helper.runOnFXThread
import insulator.jsonhelper.JsonFormatter
import insulator.kafka.AdminApi
import insulator.kafka.model.Topic
import insulator.ui.WindowsManager
import insulator.viewmodel.common.InsulatorViewModel
import insulator.views.main.topic.RecordView
import insulator.views.main.topic.TopicInfoView
import insulator.views.main.topic.TopicInfoViewModel
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableStringValue
import javafx.scene.input.Clipboard
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
    val consumerViewModel: ConsumerViewModel,
    private val formatter: JsonFormatter,
    private val windowsManager: WindowsManager
) : InsulatorViewModel() {

    private val topicProperty = SimpleObjectProperty(topic)

    val nameProperty: ObservableStringValue = Bindings.createStringBinding({ topicProperty.value.name }, topicProperty)
    val selectedItem = SimpleObjectProperty<RecordViewModel>()
    val subtitleProperty: ObservableStringValue = Bindings.createStringBinding(
        {
            val totalMessages = max(topicProperty.value.messageCount ?: 0, consumerViewModel.records.size.toLong())
            val filteredMessages = consumerViewModel.filteredRecords.value.size
            "Message count: $filteredMessages/$totalMessages - " +
                "Is internal: ${topicProperty.value.isInternal} - " +
                "Partitions count: ${topicProperty.value.partitionCount} - " +
                "Compacted: ${topicProperty.value.isCompacted}"
        },
        consumerViewModel.filteredRecords.value,
        topicProperty
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
        adminApi.describeTopic(topic.name).map { runOnFXThread { topicProperty.set(it) } }

    fun showProducerView(owner: Window?) {
        windowsManager.openWindow("producer-${topic.name}", owner) {
            topicComponent.getProducerView().also { it.whenUndocked { dispatch { refresh() } } }
        }
    }

    fun showRecordInfoView(owner: Window?) {
        if (selectedItem.value != null)
            windowsManager.openWindow("record-${selectedItem.value.hashCode()}", owner) { RecordView(selectedItem.value, formatter) }
    }

    fun showTopicInfoView(owner: Window?) {
        windowsManager.openWindow("topic-${topicProperty.value.hashCode()}", owner) { TopicInfoView(TopicInfoViewModel(topicProperty.value)) }
    }
}
