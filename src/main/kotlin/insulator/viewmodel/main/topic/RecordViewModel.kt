package insulator.viewmodel.main.topic

import javafx.beans.property.SimpleStringProperty
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class RecordViewModel(key: String?, value: String, timestamp: Long) {
    private var dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("UTC"))

    val timestampProperty = SimpleStringProperty(dateTimeFormatter.format(Instant.ofEpochMilli(timestamp)))
    val keyProperty = SimpleStringProperty(key)
    val valueProperty = SimpleStringProperty(value)

    fun toCsv() = "${this.timestampProperty.value}\t" +
        "${this.keyProperty.value}\t" +
        this.valueProperty.value
}
