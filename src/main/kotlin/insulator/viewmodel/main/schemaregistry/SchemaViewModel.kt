package insulator.viewmodel.main.schemaregistry

import com.google.gson.Gson
import com.google.gson.JsonParser
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import tornadofx.*


class SchemaViewModel(name: String, schema: String) : ViewModel() {

    private val gson: Gson by di()

    val nameProperty = SimpleStringProperty(name)

    val schemaProperty: StringProperty by lazy {
        val res = kotlin.runCatching { JsonParser.parseString(schema) }
                .map { gson.toJson(it) }
        SimpleStringProperty(res.fold({ it }, { "Unable to parse ${it.message}" }))
    }
}