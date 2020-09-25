package insulator.lib.jsonhelper

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class JsonToAvroConverterTest : FunSpec({
    test("return an hint if a field is missing - array") {
        // arrange
        val schema =
            """
            {
              "type": "record", 
              "name": "value_test_schema", 
              "namespace": "com.mycorp.mynamespace", 
              "doc": "Sample schema to help you get started.", 
              "fields": [{ "name": "testArray", "type": {"type" : "array", "items" : "string"} }]
            }
            """.trimIndent()
        val sut = JsonToAvroConverter(ObjectMapper())
        // act
        val result = sut.convert("{ }", schema)
        // assert
        result shouldBeLeft {
            it.message shouldBe "Expecting \"testArray\" with type array of \"string\""
        }
    }

    test("return an error if the schema is invalid") {
        // arrange
        val schema =
            """
            {
              "type": "record", 
              "name": "value_test_schema", 
              "namespace": "com.mycorp.mynamespace",
              "fields": [
                { "name": "testRecord", "type": "json"}
            ]
            }
            """.trimIndent()
        val json = "{ }"
        val sut = JsonToAvroConverter(ObjectMapper())
        // act
        val res = sut.convert(json, schema)
        // assert
        res shouldBeLeft {
            it.shouldBeInstanceOf<InvalidSchemaException>()
        }
    }

    test("return an hint if a field is missing - record") {
        // arrange
        val schema =
            """
            {
              "type": "record", 
              "name": "value_test_schema", 
              "namespace": "com.mycorp.mynamespace",
              "fields": [
                { "name": "testRecord", "type": 
                    {
                      "type": "record", 
                      "name": "nested",
                      "fields": [ { "name": "name", "type": "string"}]
                    }
                }
            ]
            }
            """.trimIndent()
        val json = "{ }"
        val sut = JsonToAvroConverter(ObjectMapper())
        // act
        val res = sut.convert(json, schema)
        // assert
        res shouldBeLeft {
            it.message shouldBe "Expecting \"testRecord\" with type record \"nested\""
        }
    }

    test("return an hint if a field is missing - bytes") {
        // arrange
        val schema =
            """
            {
              "type": "record", 
              "name": "value_test_schema", 
              "namespace": "com.mycorp.mynamespace",
              "fields": [
                { "name": "testBytes", "type": "bytes"}
                ]
            }
            """.trimIndent()
        val json = "{ }"
        val sut = JsonToAvroConverter(ObjectMapper())
        // act
        val res = sut.convert(json, schema)
        // assert
        res shouldBeLeft {
            it.message shouldBe "Expecting \"testBytes\" with type bytes (eg \"0x00\")"
        }
    }
})
