package insulator.viewmodel.main.schemaregistry

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.core.io.JsonEOFException
import insulator.kafka.SchemaRegistry
import insulator.viewmodel.common.InsulatorViewModel
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableBooleanValue
import javafx.beans.value.ObservableStringValue
import javax.inject.Inject

class CreateSchemaViewModel @Inject constructor(private val client: SchemaRegistry?) : InsulatorViewModel() {
    val VALID = "Valid"
    val subjectProperty = SimpleStringProperty("")
    val schemaProperty = SimpleStringProperty("")
    val validationErrorProperty: ObservableStringValue = Bindings.createStringBinding(
        {
            client!!.validate(schemaProperty.value)
                .fold({ parseException(it) }, { VALID })
        },
        schemaProperty
    )
    val isSchemaValidProperty: ObservableBooleanValue = Bindings.createBooleanBinding(
        { validationErrorProperty.value == VALID && !subjectProperty.value.isNullOrEmpty() },
        schemaProperty,
        validationErrorProperty
    )

    private fun parseException(ex: Throwable) =
        when (ex.cause) {
            is JsonParseException -> "Invalid json"
            is JsonEOFException -> "Invalid json"
            else -> ex.message
        }

    fun register() = client!!
        .register(subjectProperty.value, schemaProperty.value)
        .mapLeft { error.set(it) }
}
