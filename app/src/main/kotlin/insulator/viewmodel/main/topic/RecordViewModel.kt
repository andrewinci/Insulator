package insulator.viewmodel.main.topic

import insulator.kafka.model.Record
import insulator.viewmodel.common.InsulatorViewModel
import javafx.beans.binding.Bindings
import javafx.beans.binding.StringBinding
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleLongProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Base64

class RecordViewModel(record: Record) : InsulatorViewModel() {
    private var dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("UTC"))

    val timestampProperty = SimpleLongProperty(record.timestamp)
    val formattedTimeStampProperty = SimpleStringProperty(dateTimeFormatter.format(Instant.ofEpochMilli(record.timestamp)))
    val keyProperty = SimpleStringProperty(record.key)
    val valueProperty = SimpleStringProperty(record.value)
    val partitionProperty = SimpleIntegerProperty(record.partition)
    val offsetProperty = SimpleLongProperty(record.offset)
    val headersProperty = SimpleObjectProperty<Map<String, List<ByteArray>>>(record.headers)
    val formattedHeadersProperty: StringBinding = Bindings.createStringBinding(
        { headersProperty.value.map { (key, value) -> value.joinToString("\n") { "$key: ${Base64.getEncoder().encodeToString(it)}" } }.joinToString("\n") },
        headersProperty
    )

    fun toCsv() = "${this.partitionProperty.value}\t" +
        "${this.offsetProperty.value}\t" +
        "${this.formattedTimeStampProperty.value}\t" +
        "${this.keyProperty.value}\t" +
        this.valueProperty.value
}
