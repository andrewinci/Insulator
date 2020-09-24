package insulator.viewmodel.main.topic

import arrow.core.Either
import arrow.core.extensions.either.applicativeError.handleError
import insulator.di.getInstanceNow
import insulator.lib.jsonhelper.JsonToAvroException
import insulator.lib.kafka.Producer
import insulator.viewmodel.common.InsulatorViewModel
import javafx.beans.binding.Bindings
import javafx.beans.binding.StringBinding
import javafx.beans.property.SimpleStringProperty
import scala.util.Right
import tornadofx.*

class ProducerViewModel(val topicName: String) : InsulatorViewModel() {

    private val producer: Producer = getInstanceNow()

    val validationError = SimpleStringProperty("")
    val keyProperty = SimpleStringProperty()
    val valueProperty = SimpleStringProperty("").also {
        it.onChange { value ->
            val validMessage = "Valid message"
            if (value == null) return@onChange
            producer.validate(value, topicName).fold(
                { error ->
                    if (error is JsonToAvroException || validationError.value == validMessage)
                        validationError.set(error.message)
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
