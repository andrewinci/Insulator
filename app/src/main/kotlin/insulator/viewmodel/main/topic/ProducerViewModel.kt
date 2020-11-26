package insulator.viewmodel.main.topic

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
import javafx.beans.property.Property
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableBooleanValue
import tornadofx.onChange
import javax.inject.Inject

@TopicScope
class ProducerViewModel @Inject constructor(
    val topic: Topic,
    val cluster: Cluster,
    private val avroProducer: AvroProducer?,
    private val stringProducer: StringProducer
) : InsulatorViewModel() {

    val isTombstoneProperty = SimpleBooleanProperty(false)
    val serializeValueProperty = SimpleStringProperty(SerializationFormat.String.name)

    private val producer: Producer
        get() = when (SerializationFormat.valueOf(serializeValueProperty.value!!)) {
            SerializationFormat.Avro -> avroProducer ?: throw Exception("Null AvroProducer")
            SerializationFormat.String -> stringProducer
        }

    val nextFieldProperty = SimpleStringProperty("")
    val validationErrorProperty = SimpleStringProperty(null)
    val keyProperty = SimpleStringProperty()
    val valueProperty = SimpleStringProperty()
    val canSendProperty: ObservableBooleanValue = Bindings.createBooleanBinding(
        { ((validationErrorProperty.value == null && !valueProperty.value.isNullOrEmpty()) || isTombstoneProperty.value) && !keyProperty.value.isNullOrEmpty() },
        validationErrorProperty,
        keyProperty,
        valueProperty,
        isTombstoneProperty
    )

    init {
        if (cluster.isSchemaRegistryConfigured()) serializeValueProperty.set(SerializationFormat.Avro.name)
        listOf(valueProperty, serializeValueProperty).forEach {
            it.onChange { value ->
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
        }
        valueProperty.set("{\n}")
    }

    suspend fun send() {
        when {
            keyProperty.value.isNullOrBlank() -> error.set(Exception("Invalid key. Key must be not empty"))
            isTombstoneProperty.value -> producer.sendTombstone(topic.name, keyProperty.value)
            valueProperty.value.isNullOrBlank() -> error.set(Exception("Invalid value. Value must be not empty"))
            else -> producer.send(topic.name, keyProperty.value, valueProperty.value).mapLeft { error.set(it) }
        }
    }
}
