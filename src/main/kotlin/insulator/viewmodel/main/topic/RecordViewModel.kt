package insulator.viewmodel.main.topic

import insulator.lib.kafka.model.Record
import javafx.beans.property.SimpleStringProperty
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class RecordViewModel(val record: Record) {
    private var dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        .withZone(ZoneId.of("UTC"))
    val timestampProperty = SimpleStringProperty(dateTimeFormatter.format(Instant.ofEpochMilli(record.timestamp)))
    val keyProperty = SimpleStringProperty(record.key)
    val valueProperty = SimpleStringProperty(record.value)

    fun toCsv() = "${this.timestampProperty.value}\t" +
        "${this.keyProperty.value}\t" +
        this.valueProperty.value
}
