package insulator.lib.jsonhelper.avrotojson

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.types.shouldBeInstanceOf
import org.apache.avro.Conversions
import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericRecordBuilder
import java.math.BigDecimal
import java.nio.ByteBuffer

class AvroToJsonConverterTest : FunSpec({

    val sut = AvroToJsonConverter(ObjectMapper())
    val testCases = table(
        headers("avro type", "avro value", "json value"),
        // null
        row("\"null\"", null, "null"),
        // boolean
        row("\"boolean\"", true, true),
        row("\"boolean\"", false, false),
        row("""["boolean", "null"]""", false, false),
        row("""["null", "boolean"]""", null, "null"),
        // int, long
        row("\"int\"", -12232, -12232),
        row("\"long\"", 123243214321L, 123243214321L),
        row("""["int","null"]""", 0, 0),
        row("""["null","int"]""", null, "null"),
        // string
        row("\"string\"", "test string value", "\"test string value\""),
        row("""["string","null"]""", "test string value", "\"test string value\""),
        row("""["null","string"]""", null, "null"),
        // float, double
        row("\"double\"", -12232.123, -12232.123),
        row("\"float\"", 123243214321.123f, 123243214321.123f),
        row("""["double","null"]""", 0.32, 0.32),
        row("""["null","float"]""", null, "null"),
    )

    test("parse record with single field") {
        testCases.forAll { testType, testValue, result ->
            // arrange
            val testFieldName = "testField"
            val schema = Schema.Parser().parse(schemaTemplate("""{"name":"$testFieldName", "type":$testType }"""))
            val testRecord = GenericRecordBuilder(schema).also { it.set(testFieldName, testValue) }.build()
            // act
            val res = sut.parse(testRecord)
            // assert
            res shouldBeRight """{"$testFieldName":$result}"""
        }
    }

    test("parse record with default value") {
        testCases.forAll { testType, _, result ->
            // arrange
            val testFieldName = "testField"
            val schema = Schema.Parser().parse(schemaTemplate("""{"name":"$testFieldName", "type":$testType, "default": $result }"""))
            val testRecord = GenericRecordBuilder(schema).build()
            // act
            val res = sut.parse(testRecord)
            // assert
            res shouldBeRight """{"$testFieldName":$result}"""
        }
    }

    test("byte array type - happy path") {
        // arrange
        val testFieldName = "testField"
        val schema = Schema.Parser().parse(schemaTemplate("""{"name":"$testFieldName", "type":"bytes" }"""))
        val testRecord = GenericRecordBuilder(schema)
            .also { it.set(testFieldName, ByteBuffer.wrap(arrayOf<Byte>(0x01, 0x0d).toByteArray())) }
            .build()
        // act
        val res = sut.parse(testRecord)
        // assert
        res shouldBeRight """{"$testFieldName":"0x010d"}"""
    }

    test("byte array type - logical decimal") {
        // arrange
        val testFieldName = "testField"
        val schema = Schema.Parser().parse(schemaTemplate("""{"name":"$testFieldName", "type": {"type":"bytes", "logicalType":"decimal", "precision":4, "scale":2} }"""))
        val fieldSchema = schema.fields[0].schema()
        val testValue = Conversions.DecimalConversion().toBytes(BigDecimal.valueOf(1.23), fieldSchema, fieldSchema.logicalType)
        val testRecord = GenericRecordBuilder(schema)
            .also { it.set(testFieldName, testValue) }
            .build()
        // act
        val res = sut.parse(testRecord)
        // assert
        res shouldBeRight """{"$testFieldName":1.23}"""
    }

    test("parse array type") {
        // arrange
        val testFieldName = "testField"
        val schema = Schema.Parser().parse(schemaTemplate("""{"name":"$testFieldName", "type":{"type":"array", "items":"string"}}"""))
        val testRecord = GenericRecordBuilder(schema).also { it.set(testFieldName, listOf("1", "2", "3")) }.build()
        // act
        val res = sut.parse(testRecord)
        // assert
        res shouldBeRight """{"$testFieldName":["1","2","3"]}"""
    }

    test("parse nullable array type") {
        // arrange
        val testFieldName = "testField"
        val schema = Schema.Parser().parse(schemaTemplate("""{"name":"$testFieldName", "type":[{"type":"array", "items":"string"}, "null"]}"""))
        val testRecord = GenericRecordBuilder(schema).also { it.set(testFieldName, null) }.build()
        // act
        val res = sut.parse(testRecord)
        // assert
        res shouldBeRight """{"$testFieldName":null}"""
    }

    test("parse enum type") {
        // arrange
        val testFieldName = "testField"
        val testFieldValue = "SPADES"
        val schema = Schema.Parser().parse(
            schemaTemplate(
                """{"name":"$testFieldName", 
            |"type":{ "type": "enum", "name": "Suit", "symbols" : ["SPADES", "HEARTS", "DIAMONDS", "CLUBS"]}
            |}""".trimMargin()
            )
        )
        val avroFieldValue = GenericData.EnumSymbol(schema.fields[0].schema(), testFieldValue)
        val testRecord = GenericRecordBuilder(schema).also { it.set(testFieldName, avroFieldValue) }.build()
        // act
        val res = sut.parse(testRecord)
        // assert
        res shouldBeRight """{"$testFieldName":"$testFieldValue"}"""
    }

    test("parse nullable enum type") {
        // arrange
        val testFieldName = "testField"
        val schema = Schema.Parser().parse(
            schemaTemplate(
                """{"name":"$testFieldName", 
            |"type":[{ "type": "enum", "name": "Suit", "symbols" : ["SPADES", "HEARTS", "DIAMONDS", "CLUBS"]}, "null"]
            |}""".trimMargin()
            )
        )
        val testRecord = GenericRecordBuilder(schema).also { it.set(testFieldName, null) }.build()
        // act
        val res = sut.parse(testRecord)
        // assert
        res shouldBeRight """{"$testFieldName":null}"""
    }

    test("parse nullable enum type 2") {
        // arrange
        val testFieldName = "testField"
        val testFieldValue = "SPADES"
        val schema = Schema.Parser().parse(
            schemaTemplate(
                """{"name":"$testFieldName", 
            |"type":["null", { "type": "enum", "name": "Suit", "symbols" : ["SPADES", "HEARTS", "DIAMONDS", "CLUBS"]}]
            |}""".trimMargin()
            )
        )
        val avroFieldValue = GenericData.EnumSymbol(schema.fields[0].schema(), testFieldValue)
        val testRecord = GenericRecordBuilder(schema).also { it.set(testFieldName, avroFieldValue) }.build()
        // act
        val res = sut.parse(testRecord)
        // assert
        res shouldBeRight """{"$testFieldName":"$testFieldValue"}"""
    }

    test("not supported map") {
        // arrange
        val testFieldName = "testField"
        val testFieldValue = "SPADES"
        val schema = Schema.Parser().parse(
            schemaTemplate(
                """{"name":"$testFieldName", "type": { "type": "map", "values" : "long" }
            |}""".trimMargin()
            )
        )
        val avroFieldValue = GenericData.EnumSymbol(schema.fields[0].schema(), testFieldValue)
        val testRecord = GenericRecordBuilder(schema).also { it.set(testFieldName, avroFieldValue) }.build()
        // act
        val res = sut.parse(testRecord)
        // assert
        res shouldBeLeft {
            it.shouldBeInstanceOf<UnsupportedTypeException>()
        }
    }
})

private fun schemaTemplate(vararg fieldDef: String) =
    """
        {
          "type": "record",
          "name": "Sample",
          "fields" : [
            ${fieldDef.joinToString()}
          ]
        }
    """.trimIndent()
