package insulator.viewmodel.main.topic

import arrow.core.extensions.either.applicativeError.handleError
import insulator.lib.configuration.model.Cluster
import insulator.lib.helpers.dispatch
import insulator.lib.jsonhelper.jsontoavro.JsonFieldParsingException
import insulator.lib.jsonhelper.jsontoavro.JsonMissingFieldException
import insulator.lib.kafka.AvroProducer
import insulator.lib.kafka.Producer
import insulator.lib.kafka.SerializationFormat
import insulator.lib.kafka.StringProducer
import insulator.viewmodel.common.InsulatorViewModel
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableBooleanValue
import tornadofx.* // ktlint-disable no-wildcard-imports

class ProducerViewModel(val topicName: String) : InsulatorViewModel() {

    private val cluster: Cluster by di()
    private val avroProducer: Producer by di<AvroProducer>()
    private val stringProducer: Producer by di<StringProducer>()
    private val producer: Producer
        get() = when (producerTypeProperty.value!!) {
            SerializationFormat.Avro -> avroProducer
            SerializationFormat.String -> stringProducer
        }

    val producerTypeProperty = SimpleObjectProperty(
        if (cluster.isSchemaRegistryConfigured()) SerializationFormat.Avro else SerializationFormat.String
    )

    val nextFieldProperty = SimpleStringProperty("")
    val validationErrorProperty = SimpleStringProperty(null)
    val keyProperty = SimpleStringProperty()
    val valueProperty = SimpleStringProperty()
    val canSendProperty: ObservableBooleanValue = Bindings.createBooleanBinding(
        { validationErrorProperty.value == null && !keyProperty.value.isNullOrEmpty() && !valueProperty.value.isNullOrEmpty() },
        validationErrorProperty,
        keyProperty,
        valueProperty
    )

    init {
        valueProperty.onChange { value ->
            value?.let {
                producer.dispatch {
                    validate(value, topicName).fold(
                        { error ->
                            if (error is JsonMissingFieldException) nextFieldProperty.value = error.fieldName
                            if (error is JsonFieldParsingException || validationErrorProperty.value.isNullOrEmpty())
                                validationErrorProperty.set(error.message)
                        },
                        { validationErrorProperty.set(null) }
                    )
                }
            }
        }
        valueProperty.set("{\n}")
    }

    suspend fun send() {
        if (keyProperty.value.isNullOrBlank()) {
            error.set(Exception("Invalid key. Key must be not empty"))
            return
        }
        if (valueProperty.value.isNullOrBlank()) {
            error.set(Exception("Invalid value. Value must be not empty"))
            return
        }
        producer.send(topicName, keyProperty.value, valueProperty.value)
            .handleError { error.set(it) }
    }
}
