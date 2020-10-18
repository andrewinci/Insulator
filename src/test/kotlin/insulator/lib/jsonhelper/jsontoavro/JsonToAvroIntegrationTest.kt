package insulator.lib.jsonhelper.jsontoavro

import com.fasterxml.jackson.databind.ObjectMapper
import insulator.lib.jsonhelper.jsontoavro.fieldparser.ComplexTypeParsersFactory
import insulator.lib.jsonhelper.jsontoavro.fieldparser.SimpleTypeParsersFactory
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
