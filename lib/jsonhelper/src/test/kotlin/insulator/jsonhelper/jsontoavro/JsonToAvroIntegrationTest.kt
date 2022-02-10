package insulator.jsonhelper.jsontoavro

import com.fasterxml.jackson.databind.ObjectMapper
import insulator.jsonhelper.jsontoavro.fieldparser.ComplexTypeParsersFactory
import insulator.jsonhelper.jsontoavro.fieldparser.SimpleTypeParsersFactory
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.FreeSpec
import org.apache.avro.generic.GenericData

class JsonToAvroIntegrationTest : FreeSpec({

    "Happy path" - {
        // arrange
        val fieldParser = FieldParser(SimpleTypeParsersFactory(), ComplexTypeParsersFactory())
        val sut = JsonToAvroConverter(ObjectMapper(), fieldParser, GenericData.get())

        "parse a schema with only a string" {
            val schema = schemaTemplate("""{"name":"test", "type":"string"}""")
            val json =
                """{"test":"123"}"""
            // act
            val res = sut.parse(json, schema)
            // assert
            res shouldBeRight {}
        }

        "left if not all fields are used " {
            val schema = schemaTemplate("""{"name":"test", "type":"string"}""")
            val json =
                """{"test":"123", "unused": 321}"""
            // act
            val res = sut.parse(json, schema)
            // assert
            res shouldBeLeft {}
        }
    }
})

fun schemaTemplate(vararg fieldDef: String) =
    """
        {
          "type": "record",
          "name": "Sample",
          "fields" : [
            ${fieldDef.joinToString()}
          ]
        }
    """.trimIndent()
