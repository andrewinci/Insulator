package insulator.viewmodel.main.topic

import insulator.lib.configuration.model.Cluster
import insulator.lib.helpers.dispatch
import insulator.lib.helpers.runOnFXThread
import insulator.lib.kafka.AdminApi
import insulator.lib.kafka.Consumer
import insulator.lib.kafka.model.Topic
import insulator.ui.common.topicScope
import insulator.viewmodel.common.InsulatorViewModel
import insulator.views.main.topic.ProducerView
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableStringValue
import javafx.scene.input.Clipboard
import javafx.stage.Modality
import javafx.stage.StageStyle
import tornadofx.find
import tornadofx.putString

class TopicViewModel(val topicName: String) : InsulatorViewModel() {

    private val cluster: Cluster by di()
    private val adminApi: AdminApi by di()
    private val consumer: Consumer by di()

    private val topicProperty = SimpleObjectProperty<Topic?>()

    val consumerViewModel = ConsumerViewModel(consumer, topicName)

    val nameProperty = SimpleStringProperty(topicName)
    val selectedItem = SimpleObjectProperty<RecordViewModel>()
    val subtitleProperty: ObservableStringValue = Bindings.createStringBinding(
        {
            "Message count: ${consumerViewModel.filteredRecords.value.size}/${consumerViewModel.records.size} - " +
                "Total records: ${topicProperty.value?.messageCount} - " +
                "Is internal: ${topicProperty.value?.isInternal} - " +
                "Partitions count: ${topicProperty.value?.partitionCount} - " +
                "Compacted: ${topicProperty.value?.isCompacted}"
        },
        consumerViewModel.filteredRecords.value,
        topicProperty,
        consumerViewModel.records
    )

    suspend fun delete() =
        adminApi.deleteTopic(this.nameProperty.value)

    fun copySelectedRecordToClipboard() =
        selectedItem.value?.let {
            Clipboard.getSystemClipboard().putString(selectedItem.value.toCsv())
        }

    fun copyAllRecordsToClipboard() =
        Clipboard.getSystemClipboard().putString(consumerViewModel.filteredRecords.value.joinToString("\n") { it.toCsv() })

    private suspend fun refresh() {
        adminApi.describeTopic(topicName)
            .map { topicProperty.runOnFXThread { set(it) } }
    }

    init {
        dispatch { refresh() }
    }

    fun showProduceView() = topicName
        .topicScope(cluster)
        .withComponent(ProducerViewModel(topicName))
        .let { find<ProducerView>(it) }
        .openWindow(modality = Modality.WINDOW_MODAL, stageStyle = StageStyle.UTILITY)
}
