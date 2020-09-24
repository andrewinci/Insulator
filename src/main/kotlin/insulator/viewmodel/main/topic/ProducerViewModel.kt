package insulator.viewmodel.main.topic

import arrow.core.extensions.either.applicativeError.handleError
import insulator.di.getInstanceNow
import insulator.lib.kafka.Producer
import insulator.viewmodel.common.InsulatorViewModel
import javafx.beans.property.SimpleStringProperty

class ProducerViewModel(val topicName: String) : InsulatorViewModel() {

    private val producer: Producer = getInstanceNow()

    val keyProperty = SimpleStringProperty()
    val valueProperty = SimpleStringProperty()

    fun send() {
        if (keyProperty.value.isNullOrBlank()) {
            error.set(Exception("Invalid key. Key must be not empty"))
            return
        }
        if (valueProperty.value.isNullOrBlank()) {
            error.set(Exception("Invalid value. Value must be not empty"))
            return
        }
        producer.produce(topicName, keyProperty.value, valueProperty.value)
            .handleError { error.set(it) }
    }
}
