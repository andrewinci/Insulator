package insulator.viewmodel.main.topic

import insulator.kafka.model.Record
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleLongProperty
import javafx.beans.property.SimpleStringProperty
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class RecordViewModel(record: Record) {
    private var dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("UTC"))

    val timestampProperty = SimpleStringProperty(dateTimeFormatter.format(Instant.ofEpochMilli(record.timestamp)))
    val keyProperty = SimpleStringProperty(record.key)
    val valueProperty = SimpleStringProperty(record.value)
    val partitionProperty = SimpleIntegerProperty(record.partition)
    val offsetProperty = SimpleLongProperty(record.offset)

    fun toCsv() = "${this.partitionProperty.value}\t" +
        "${this.offsetProperty.value}\t" +
        "${this.timestampProperty.value}\t" +
        "${this.keyProperty.value}\t" +
        this.valueProperty.value
}
