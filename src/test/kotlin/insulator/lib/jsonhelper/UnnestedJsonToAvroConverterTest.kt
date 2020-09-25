package insulator.lib.jsonhelper

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.apache.avro.Conversions
import java.math.BigDecimal
import java.nio.ByteBuffer

class UnnestedJsonToAvroConverterTest : FunSpec({

    test("Invalid json return left") {
        // arrange
        val sut = JsonToAvroConverter(ObjectMapper())
        // act
        val result = sut.convert("invalid json", "{todo: fix schema}")
        // assert
        result shouldBeLeft {}
    }

    test("Convert unnested json with only primitive types to avro") {
        // arrange
        val schema =
            """
            {
              "type": "record", 
              "name": "value_test_schema", 
              "namespace": "com.mycorp.mynamespace",
              "fields": [
                { "name": "testNull", "type": "null"},
                { "name": "testBoolean", "type": "boolean"},
                { "name": "testInt", "type": "int"},
                { "name": "testLong", "type": "long"},
                { "name": "testLong2", "type": "long"},
                { "name": "testFloat", "type": "float"},
                { "name": "testDouble", "type": "double"},
                { "name": "testBytes", "type": "bytes"},
                { "name": "testString", "type": "string"}
                ]
            }
            """.trimIndent()
        val sampleJson =
            """{
            "testNull": null,
            "testBoolean": false,
            "testInt": 123,
            "testLong": 123123123123123123,
            "testLong2": 12,
            "testFloat": 4.566,
            "testDouble": 1.234,
            "testBytes": "0x01021F",
            "testString": "string"
        }
            """.trimIndent()
        val sut = JsonToAvroConverter(ObjectMapper())
        // act
        val result = sut.convert(sampleJson, schema)
        // assert
        result shouldBeRight {
            it.get("testNull") shouldBe null
            it.get("testBoolean") shouldBe false
            it.get("testInt") shouldBe 123
            it.get("testLong") shouldBe 123123123123123123
            it.get("testFloat") shouldBe 4.566f
            it.get("testDouble") shouldBe 1.234
            (it.get("testBytes") as ByteBuffer).array() shouldBe byteArrayOf(0x01, 0x02, 0x1f)
            it.get("testString") shouldBe "string"
        }
    }

    test("Convert json only to avro - union type left branch") {
        // arrange
        val schema =
            """
            {
              "type": "record", 
              "name": "value_test_schema", 
              "namespace": "com.mycorp.mynamespace",
              "fields": [{
                  "name": "test", 
                  "type": ["null", "double"], 
                  "default": null
                }]
            }
            """.trimIndent()
        val sampleJson =
            """{ "test": null}"""
        val sut = JsonToAvroConverter(ObjectMapper())
        // act
        val result = sut.convert(sampleJson, schema)
        // assert
        result shouldBeRight {
            it.get("test") shouldBe null
        }
    }

    test("Convert json only to avro - union type right branch") {
        // arrange
        val schema =
            """
            {
              "type": "record", 
              "name": "value_test_schema", 
              "namespace": "com.mycorp.mynamespace",
              "fields": [{
                  "name": "test", 
                  "type": ["null", "double"], 
                  "default": null
                }]
            }
            """.trimIndent()
        val sampleJson =
            """{ "test": 1.232}"""
        val sut = JsonToAvroConverter(ObjectMapper())
        // act
        val result = sut.convert(sampleJson, schema)
        // assert
        result shouldBeRight {
            it.get("test") shouldBe 1.232
        }
    }

    test("Convert json to avro - union - logical type - left branch") {
        // arrange
        val schema =
            """
            {
              "type": "record", 
              "name": "value_test_schema", 
              "namespace": "com.mycorp.mynamespace", 
              "doc": "Sample schema to help you get started.", 
              "fields": [{
                  "name": "test", 
                  "type": ["null", {
                      "type": "bytes", 
                      "logicalType": "decimal", 
                      "precision": 5, 
                      "scale": 2
                    }]
                }]
            }
            """.trimIndent()
        val sut = JsonToAvroConverter(ObjectMapper())
        // act
        val result = sut.convert("{ \"test\": null }", schema)
        // assert
        result shouldBeRight {
            it.get("test") shouldBe null
        }
    }

    test("Convert json to avro - union - logical type - right branch") {
        // arrange
        val schema =
            """
            {
              "type": "record", 
              "name": "value_test_schema", 
              "namespace": "com.mycorp.mynamespace", 
              "doc": "Sample schema to help you get started.", 
              "fields": [{
                  "name": "test", 
                  "type": ["null", {
                      "type": "bytes", 
                      "logicalType": "decimal", 
                      "precision": 5, 
                      "scale": 2
                    }]
                }]
            }
            """.trimIndent()
        val sut = JsonToAvroConverter(ObjectMapper())
        // act
        val result = sut.convert("{ \"test\": 1.34 }", schema)
        // assert
        result shouldBeRight {
            val decimalFieldSchema = it.schema.fields[0].schema().types[1]
            val value = it.get("test") as ByteBuffer
            val convertedResult = Conversions.DecimalConversion().fromBytes(value, decimalFieldSchema, decimalFieldSchema.logicalType)
            convertedResult shouldBe BigDecimal.valueOf(134, 2)
        }
    }

    test("Convert json to avro - array of primitive types") {
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
        val result = sut.convert("{ \"testArray\": [\"str1\", \"str2\"] }", schema)
        // assert
        result shouldBeRight {
            it.get("testArray") shouldBe listOf("str1", "str2")
        }
    }

    test("Convert json to avro - array of primitive types with invalid json") {
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
        val result = sut.convert("{ \"testArray\": [\"str1\", 1] }", schema)
        // assert
        result shouldBeLeft {}
    }

    test("Convert json to avro - enum") {
        // arrange
        val schema =
            """
            {
              "type": "record", 
              "name": "value_test_schema", 
              "namespace": "com.mycorp.mynamespace", 
              "doc": "Sample schema to help you get started.", 
              "fields": [{ "name": "testEnum", "type": { "type" : "enum",
                  "name" : "Colors",
                  "namespace" : "palette",
                  "doc" : "Colors supported by the palette.",
                  "symbols" : ["WHITE", "BLUE", "GREEN", "RED", "BLACK"]} }]
            }
            """.trimIndent()
        val sut = JsonToAvroConverter(ObjectMapper())
        // act
        val result = sut.convert("{ \"testEnum\": \"WHITE\" }", schema)
        // assert
        result shouldBeRight {
            it.get("testEnum").toString() shouldBe "WHITE"
        }
    }

    test("Convert json to avro - enum with invalid json") {
        // arrange
        val schema =
            """
            {
              "type": "record", 
              "name": "value_test_schema", 
              "namespace": "com.mycorp.mynamespace", 
              "doc": "Sample schema to help you get started.", 
              "fields": [{ "name": "testEnum", "type": { "type" : "enum",
                  "name" : "Colors",
                  "namespace" : "palette",
                  "doc" : "Colors supported by the palette.",
                  "symbols" : ["WHITE", "BLUE", "GREEN", "RED", "BLACK"]} }]
            }
            """.trimIndent()
        val sut = JsonToAvroConverter(ObjectMapper())
        // act
        val result = sut.convert("{ \"testEnum\": \"GRAY\" }", schema)
        // assert
        result shouldBeLeft {}
    }
})
