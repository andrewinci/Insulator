package insulator.viewmodel.main.topic

import arrow.core.extensions.either.applicativeError.handleError
import insulator.di.TopicScope
import insulator.lib.configuration.model.Cluster
import insulator.lib.helpers.dispatch
import insulator.lib.jsonhelper.jsontoavro.JsonFieldParsingException
import insulator.lib.jsonhelper.jsontoavro.JsonMissingFieldException
import insulator.lib.kafka.AvroProducer
import insulator.lib.kafka.Producer
import insulator.lib.kafka.SerializationFormat
import insulator.lib.kafka.StringProducer
import insulator.lib.kafka.model.Topic
import insulator.viewmodel.common.InsulatorViewModel
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableBooleanValue
import tornadofx.* // ktlint-disable no-wildcard-imports
import javax.inject.Inject

@TopicScope
class ProducerViewModel @Inject constructor(
    val topic: Topic,
    val cluster: Cluster,
    private val avroProducer: AvroProducer,
    private val stringProducer: StringProducer
) : InsulatorViewModel() {

    val producerTypeProperty = SimpleObjectProperty(
        if (cluster.isSchemaRegistryConfigured()) SerializationFormat.Avro else SerializationFormat.String
    )

    private val producer: Producer
        get() = when (producerTypeProperty.value!!) {
            SerializationFormat.Avro -> avroProducer
            SerializationFormat.String -> stringProducer
        }

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
                    validate(value, topic.name).fold(
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
        producer.send(topic.name, keyProperty.value, valueProperty.value)
            .handleError { error.set(it) }
    }
}
