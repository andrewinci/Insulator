package insulator.jsonhelper.avrotojson

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.StringSpec
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

class AvroToJsonConverterTest : StringSpec({

    val sut = AvroToJsonConverter(ObjectMapper())
    val testCases = table(
        headers("avro type", "avro value", "json value"),
        // null
        row("\"null\"", null, "null"),
        // boolean
        row("\"boolean\"", b = true, c = true),
        row("\"boolean\"", b = false, c = false),
        row("""["boolean", "null"]""", b = false, c = false),
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

    "parse record with single field" {
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

    "parse record with default value" {
        testCases.forAll { testType, _, result ->
            // arrange
            val testFieldName = "testField"
            val schema = Schema.Parser()
                .parse(schemaTemplate("""{"name":"$testFieldName", "type":$testType, "default": $result }"""))
            val testRecord = GenericRecordBuilder(schema).build()
            // act
            val res = sut.parse(testRecord)
            // assert
            res shouldBeRight """{"$testFieldName":$result}"""
        }
    }

    "byte array type - happy path" {
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

    "byte array type - logical decimal" {
        // arrange
        val testFieldName = "testField"
        val schema = Schema.Parser()
            .parse(schemaTemplate("""{"name":"$testFieldName", "type": {"type":"bytes", "logicalType":"decimal", "precision":4, "scale":2} }"""))
        val fieldSchema = schema.fields[0].schema()
        val testValue =
            Conversions.DecimalConversion().toBytes(BigDecimal.valueOf(1.23), fieldSchema, fieldSchema.logicalType)
        val testRecord = GenericRecordBuilder(schema)
            .also { it.set(testFieldName, testValue) }
            .build()
        // act
        val res = sut.parse(testRecord, true)
        // assert
        res shouldBeRight """{"$testFieldName":1.23}"""
    }

    "parse array type" {
        // arrange
        val testFieldName = "testField"
        val schema = Schema.Parser()
            .parse(schemaTemplate("""{"name":"$testFieldName", "type":{"type":"array", "items":"string"}}"""))
        val testRecord = GenericRecordBuilder(schema).also { it.set(testFieldName, listOf("1", "2", "3")) }.build()
        // act
        val res = sut.parse(testRecord)
        // assert
        res shouldBeRight """{"$testFieldName":["1","2","3"]}"""
    }

    "parse nullable array type" {
        // arrange
        val testFieldName = "testField"
        val schema = Schema.Parser()
            .parse(schemaTemplate("""{"name":"$testFieldName", "type":[{"type":"array", "items":"string"}, "null"]}"""))
        val testRecord = GenericRecordBuilder(schema).also { it.set(testFieldName, null) }.build()
        // act
        val res = sut.parse(testRecord)
        // assert
        res shouldBeRight """{"$testFieldName":null}"""
    }

    "parse enum type" {
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

    "parse nullable enum type" {
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

    "parse nullable enum type 2" {
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

    "parse date" {
        // arrange
        val testFieldName = "testField"
        val testFieldValue = "2022-04-12"
        val schema = Schema.Parser().parse(
            schemaTemplate(
                """{"name":"$testFieldName", "type":{ "type": "int", "logicalType": "date"} }"""
            )
        )
        val avroFieldValue = 19094 // 12 april 2022
        val testRecord = GenericRecordBuilder(schema).also { it.set(testFieldName, avroFieldValue) }.build()
        // act
        val res = sut.parse(testRecord, true)
        // assert
        res shouldBeRight """{"$testFieldName":"$testFieldValue"}"""
    }

    "parse timestamp-millis" {
        // arrange
        val testFieldName = "testField"
        val testFieldValue = "2022-04-12T05:22:47Z"
        val schema = Schema.Parser().parse(
            schemaTemplate(
                """{"name":"$testFieldName", "type":{ "type": "long", "logicalType": "timestamp-millis"} }"""
            )
        )
        val avroFieldValue = 1649740967000 // Tue Apr 12 2022 05:22:47 GMT+0000
        val testRecord = GenericRecordBuilder(schema).also { it.set(testFieldName, avroFieldValue) }.build()
        // act
        val res = sut.parse(testRecord, true)
        // assert
        res shouldBeRight """{"$testFieldName":"$testFieldValue"}"""
    }

    "parse time-millis" {
        // arrange
        val testFieldName = "testField"
        val testFieldValue = "03:25:45.123"
        val schema = Schema.Parser().parse(
            schemaTemplate(
                """{"name":"$testFieldName", "type":{ "type": "int", "logicalType": "time-millis"} }"""
            )
        )
        val avroFieldValue = 12345123 // 03:25:45.123
        val testRecord = GenericRecordBuilder(schema).also { it.set(testFieldName, avroFieldValue) }.build()
        // act
        val res = sut.parse(testRecord, true)
        // assert
        res shouldBeRight """{"$testFieldName":"$testFieldValue"}"""
    }

    "not supported map" {
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

    "parse union of records" {
        // arrange
        val schema = Schema.Parser().parse(
            schemaTemplate(
                """{"name":"union","type":[{"type":"record","name":"record1","fields":[{"name":"field1","type":"string"},{"name":"field2","type":"string"}]},{"type":"record","name":"record2","fields":[{"name":"field3","type":"string"}]}]}"""
            )
        )
        val nestedRecord = GenericRecordBuilder(schema.fields[0].schema().types[0]).also {
            it.set("field1", "value1")
            it.set("field2", "value2")
        }.build()
        val testRecord = GenericRecordBuilder(schema).also { it.set("union", nestedRecord) }.build()
        // act
        val res = sut.parse(testRecord)
        // assert
        res shouldBeRight  {}
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
