package insulator.lib.jsonhelper.jsontoavro.fieldparser

import insulator.lib.jsonhelper.jsontoavro.JsonFieldParsingException
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeInstanceOf
import org.apache.avro.Conversions
import org.apache.avro.Schema
import java.nio.ByteBuffer

class ByteParserTest : FunSpec({

    test("happy path - bytes") {
        // arrange
        val sut = ByteParser()
        val schema = Schema.Parser().parse("""{ "type": "bytes"}""")
        val binaryString = "0x00010203"
        // act
        val res = sut.parse(binaryString, schema)
        // assert
        res shouldBeRight ByteBuffer.wrap(byteArrayOf(0, 1, 2, 3))
    }

    test("happy path - decimal") {
        // arrange
        val sut = ByteParser()
        val schema = Schema.Parser().parse("""{ "type": "bytes", "logicalType": "decimal", "precision": 4, "scale": 2}""")
        val decimal = 1.12
        // act
        val res = sut.parse(decimal, schema)
        // assert
        res shouldBeRight {
            Conversions.DecimalConversion().toBytes(decimal.toBigDecimal(), schema, schema.logicalType)
        }
    }

    test("parsing a decimal set the scale to the schema one") {
        // arrange
        val sut = ByteParser()
        val schema = Schema.Parser().parse("""{ "type": "bytes", "logicalType": "decimal", "precision": 6, "scale": 4}""")
        val decimal = 1.3
        // act
        val res = sut.parse(decimal, schema)
        // assert
        res shouldBeRight {
            val scaledDecimal = decimal.toBigDecimal().setScale(4)
            Conversions.DecimalConversion().toBytes(scaledDecimal, schema, schema.logicalType)
        }
    }

    test("parsing a decimal return left if exceed scale") {
        // arrange
        val sut = ByteParser()
        val schema = Schema.Parser().parse("""{ "type": "bytes", "logicalType": "decimal", "precision": 6, "scale": 4}""")
        val decimal = 1.312345
        // act
        val res = sut.parse(decimal, schema)
        // assert
        res shouldBeLeft {}
    }

    test("return left if try to parse number to bytes") {
        // arrange
        val sut = ByteParser()
        val schema = Schema.Parser().parse("""{ "type": "bytes"}""")
        val decimal = 1.12
        // act
        val res = sut.parse(decimal, schema)
        // assert
        res shouldBeLeft {
            it.shouldBeInstanceOf<JsonFieldParsingException>()
        }
    }

    test("return left if try to parse an int to decimal") {
        // arrange
        val sut = ByteParser()
        val schema = Schema.Parser().parse("""{ "type": "bytes", "logicalType": "decimal", "precision": 6, "scale": 4}""")
        val integer = 1
        // act
        val res = sut.parse(integer, schema)
        // assert
        res shouldBeLeft {
            it.shouldBeInstanceOf<JsonFieldParsingException>()
        }
    }

    test("return left if try to parse an invalid string to bytes") {
        // arrange
        val sut = ByteParser()
        val schema = Schema.Parser().parse("""{ "type": "bytes"}""")
        val invalidString = "0123"
        // act
        val res = sut.parse(invalidString, schema)
        // assert
        res shouldBeLeft {
            it.shouldBeInstanceOf<JsonFieldParsingException>()
        }
    }
})
