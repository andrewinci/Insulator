package insulator.viewmodel.main.topic

import arrow.core.extensions.either.applicativeError.handleError
import insulator.di.getInstanceNow
import insulator.lib.configuration.model.Cluster
import insulator.lib.jsonhelper.JsonToAvroException
import insulator.lib.kafka.AvroProducer
import insulator.lib.kafka.Producer
import insulator.lib.kafka.StringProducer
import insulator.viewmodel.common.InsulatorViewModel
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableBooleanValue
import tornadofx.* // ktlint-disable no-wildcard-imports

class ProducerViewModel(val topicName: String) : InsulatorViewModel() {

    val cluster: Cluster by di()
    private var producer: Producer

    val producerTypeProperty = SimpleStringProperty("")
    val nextFieldProperty = SimpleStringProperty("")
    val validationErrorProperty = SimpleStringProperty(null)
    val keyProperty = SimpleStringProperty()
    val valueProperty = SimpleStringProperty("{\n}")
    val canSendProperty: ObservableBooleanValue = Bindings.createBooleanBinding(
        { validationErrorProperty.value == null && !keyProperty.value.isNullOrEmpty() && !valueProperty.value.isNullOrEmpty() },
        validationErrorProperty,
        keyProperty,
        valueProperty
    )

    init {
        if (cluster.isSchemaRegistryConfigured()) {
            producer = getInstanceNow<AvroProducer>()
            producerTypeProperty.set("Avro Producer")
        } else {
            producer = getInstanceNow<StringProducer>()
            producerTypeProperty.set("String Producer")
        }
        valueProperty.onChange { value ->
            if (value != null) {
                producer.validate(value, topicName).fold(
                    { error ->
                        if (error is JsonToAvroException || validationErrorProperty.value == null) {
                            validationErrorProperty.set(error.message)
                        }
                        val nextField = (error as? JsonToAvroException)?.nextField
                        if (nextField != null) nextFieldProperty.value = nextField
                    },
                    { validationErrorProperty.set(null) }
                )
            }
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
