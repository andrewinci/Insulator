package insulator.viewmodel.main.topic

import arrow.core.extensions.either.applicativeError.handleError
import insulator.di.TopicScope
import insulator.helper.dispatch
import insulator.jsonhelper.jsontoavro.JsonFieldParsingException
import insulator.jsonhelper.jsontoavro.JsonMissingFieldException
import insulator.kafka.model.Cluster
import insulator.kafka.model.Topic
import insulator.kafka.producer.AvroProducer
import insulator.kafka.producer.Producer
import insulator.kafka.producer.SerializationFormat
import insulator.kafka.producer.StringProducer
import insulator.viewmodel.common.InsulatorViewModel
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableBooleanValue
import tornadofx.onChange
import javax.inject.Inject

@TopicScope
class ProducerViewModel @Inject constructor(
    val topic: Topic,
    val cluster: Cluster,
    private val avroProducer: AvroProducer,
    private val stringProducer: StringProducer
) : InsulatorViewModel() {

    val serializeValueProperty = SimpleStringProperty()
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
