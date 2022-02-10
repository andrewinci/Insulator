package insulator.viewmodel.main.topic

import insulator.di.TopicScope
import insulator.helper.createListBindings
import insulator.helper.dispatch
import insulator.jsonhelper.jsontoavro.JsonFieldParsingException
import insulator.jsonhelper.jsontoavro.JsonMissingFieldException
import insulator.kafka.SchemaRegistry
import insulator.kafka.model.Cluster
import insulator.kafka.model.Schema
import insulator.kafka.model.Subject
import insulator.kafka.model.Topic
import insulator.kafka.producer.AvroProducer
import insulator.kafka.producer.Producer
import insulator.kafka.producer.SerializationFormat
import insulator.kafka.producer.StringProducer
import insulator.viewmodel.common.InsulatorViewModel
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableBooleanValue
import javafx.collections.ObservableList
import tornadofx.onChange
import javax.inject.Inject

@TopicScope
class ProducerViewModel @Inject constructor(
    val topic: Topic,
    val cluster: Cluster,
    private val avroProducer: AvroProducer?,
    private val schemaRegistry: SchemaRegistry?,
    private val stringProducer: StringProducer
) : InsulatorViewModel() {

    val isTombstoneProperty = SimpleBooleanProperty(false)
    val serializationFormatProperty = SimpleObjectProperty(SerializationFormat.String)

    private val producer: Producer
        get() = when (serializationFormatProperty.value) {
            SerializationFormat.Avro -> avroProducer ?: throw Exception("Null AvroProducer")
            SerializationFormat.String -> stringProducer
            null -> throw Exception("Invalid serialization format")
        }

    private val subjectProperty = SimpleObjectProperty<Subject?>()
    val versionsProperty: ObservableList<Schema> =
        createListBindings({ subjectProperty.value?.schemas ?: emptyList() }, subjectProperty)
    val selectedVersionProperty = SimpleObjectProperty<Schema?>()

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
        if (cluster.isSchemaRegistryConfigured()) {
            serializationFormatProperty.set(SerializationFormat.Avro)
            schemaRegistry?.dispatch {
                val subjects = getSubject("${topic.name}-value")
                subjects.fold(
                    { validationErrorProperty.set("Unable to retrieve the list of schemas for the topic: ${topic.name}") },
                    {
                        subjectProperty.set(it)
                        selectedVersionProperty.set(subjectProperty.value?.schemas?.last())
                    })
            }
        }
        listOf(valueProperty, serializationFormatProperty, selectedVersionProperty).forEach {
            it.onChange {
                producer.dispatch {
                    validate(valueProperty.value, topic.name, selectedVersionProperty.value?.version).fold(
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
        when {
            keyProperty.value.isNullOrBlank() -> error.set(Exception("Invalid key. Key must be not empty"))
            isTombstoneProperty.value -> producer.sendTombstone(topic.name, keyProperty.value)
            valueProperty.value.isNullOrBlank() -> error.set(Exception("Invalid value. Value must be not empty"))
            else -> producer.send(topic.name, keyProperty.value, valueProperty.value).mapLeft { error.set(it) }
        }
    }
}
