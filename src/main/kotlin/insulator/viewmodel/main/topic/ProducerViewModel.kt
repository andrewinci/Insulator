package insulator.viewmodel.main.topic

import arrow.core.extensions.either.applicativeError.handleError
import insulator.di.getInstanceNow
import insulator.lib.jsonhelper.JsonToAvroException
import insulator.lib.kafka.Producer
import insulator.viewmodel.common.InsulatorViewModel
import javafx.beans.property.SimpleStringProperty
import tornadofx.* // ktlint-disable no-wildcard-imports

class ProducerViewModel(val topicName: String) : InsulatorViewModel() {

    private val producer: Producer = getInstanceNow()

    val nextFieldProperty = SimpleStringProperty("")
    val validationError = SimpleStringProperty("")
    val keyProperty = SimpleStringProperty()
    val valueProperty = SimpleStringProperty("{\n}").also {
        it.onChange { value ->
            val validMessage = ""
            if (value == null) return@onChange
            producer.validate(value, topicName).fold(
                { error ->
                    if (error is JsonToAvroException || validationError.value == validMessage) {
                        validationError.set(error.message)
                    }
                    val nextField = (error as? JsonToAvroException)?.nextField
                    if (nextField != null) nextFieldProperty.value = nextField
                },
                { validationError.set(validMessage) }
            )
        }
    }

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
