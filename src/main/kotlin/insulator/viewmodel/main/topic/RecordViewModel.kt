package insulator.viewmodel.main.topic

import javafx.beans.property.SimpleStringProperty
import java.time.Instant

class RecordViewModel(key: String, value: String, timestamp: Long) {
    val timestamp = SimpleStringProperty(Instant.ofEpochMilli(timestamp).toString())
    val keyProperty = SimpleStringProperty(key)
    val valueProperty = SimpleStringProperty(value)
}