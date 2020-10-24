package insulator.jsonhelper.jsontoavro

import arrow.core.right
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import org.apache.avro.generic.GenericRecord

class JsonToAvroConverterTest : StringSpec({
    "return left if the json provided is invalid" {
        // arrange
        val sut = JsonToAvroConverter(ObjectMapper(), mockk(), mockk())
        // act
        val res = sut.parse("invalid json!!", testSchema)
        // assert
        res shouldBeLeft {
            it.shouldBeInstanceOf<JsonParsingException>()
        }
    }

    "return left if the schema provided is invalid" {
        // arrange
        val sut = JsonToAvroConverter(ObjectMapper(), mockk(), mockk())
        // act
        val res = sut.parse("{}", "invalid schema!")
        // assert
        res shouldBeLeft {
            it.shouldBeInstanceOf<SchemaParsingException>()
        }
    }

    "return the record parse value if the input schema and json are correct" {
        // arrange
        val mockOutputRecord = mockk<GenericRecord>()
        val sut = JsonToAvroConverter(
            ObjectMapper(),
            mockk {
                every { parseField(any(), any()) } returns mockOutputRecord.right()
            },
            mockk {
                every { validate(any(), any()) } returns true
            }
        )
        // act
        val res = sut.parse("{}", testSchema)
        // assert
        res shouldBeRight mockOutputRecord
    }
})

private val testSchema =
    """
    {
      "type": "record", 
      "name": "value_test_schema", 
      "namespace": "com.mycorp.mynamespace", 
      "doc": "Sample schema to help you get started.", 
      "fields": [{ "name": "testArray", "type": {"type" : "array", "items" : "string"} }]
    }
    """.trimIndent()
