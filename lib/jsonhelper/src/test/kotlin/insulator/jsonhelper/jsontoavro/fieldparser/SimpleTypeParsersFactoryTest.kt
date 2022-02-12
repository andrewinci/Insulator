package insulator.jsonhelper.jsontoavro.fieldparser

import insulator.jsonhelper.jsontoavro.JsonInvalidFieldException
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.should
import io.kotest.matchers.types.shouldBeInstanceOf
import org.apache.avro.Schema
import org.apache.avro.generic.GenericData

class SimpleTypeParsersFactoryTest : StringSpec({

    val sut = SimpleTypeParsersFactory().build()

    "happy path" {
        table(
            headers("test value", "schema", "parser", "result"),
            // int parser
            row(123123, """{"type":"int"}""", sut.intParser, 123123),
            row(-1231231, """{"type":"int"}""", sut.intParser, -1231231),
            row(0, """{"type":"int"}""", sut.intParser, 0),
            // long parser
            row(321, """{"type":"long"}""", sut.longParser, 321L),
            row(123123, """{"type":"long"}""", sut.longParser, 123123L),
            row(-1231231, """{"type":"long"}""", sut.longParser, -1231231L),
            row(32132131231232131, """{"type":"long"}""", sut.longParser, 32132131231232131L),
            // float parser
            row(1234.5678f, """{"type":"float"}""", sut.floatParser, 1234.5678f),
            row(23.0, """{"type":"float"}""", sut.floatParser, 23.0f),
            row(-1.21, """{"type":"float"}""", sut.floatParser, -1.21f),
            row(4312412412.4214214124, """{"type":"float"}""", sut.floatParser, 4312412412.4214214124f),
            // double parser
            row(23.0, """{"type":"double"}""", sut.doubleParser, 23.0),
            row(-1.21, """{"type":"double"}""", sut.doubleParser, -1.21),
            row(4312412412.4214214124, """{"type":"double"}""", sut.doubleParser, 4312412412.4214214124),
            // null parser
            row(null, """{"type":"null"}""", sut.nullParser, null),
            // boolean parser
            row(true, """{"type":"boolean"}""", sut.booleanParser, true),
            row(false, """{"type":"boolean"}""", sut.booleanParser, false),
            row("true", """{"type":"boolean"}""", sut.booleanParser, true),
            row("TrUE", """{"type":"boolean"}""", sut.booleanParser, true),
            row("FaLsE", """{"type":"boolean"}""", sut.booleanParser, false),
            row("false", """{"type":"boolean"}""", sut.booleanParser, false),
            // string parser
            row("", """{"type":"string"}""", sut.stringParser, ""),
            row("random string with spaces", """{"type":"string"}""", sut.stringParser, "random string with spaces"),
            row("dsfdafaf", """{"type":"string"}""", sut.stringParser, "dsfdafaf"),
            row("test1\ttest2\ntest3\n\r", """{"type":"string"}""", sut.stringParser, "test1\ttest2\ntest3\n\r"),

        ).forAll { testValue, stringSchema, parser, result ->

            val schema = Schema.Parser().parse(stringSchema)
            // act
            val res = parser.parse(testValue, schema)
            // assert
            res shouldBeRight result
        }
    }

    "parse invalid field" {
        table(
            headers("test value", "schema", "parser"),
            // int parser
            row(3232131312312322123L, """{"type":"int"}""", sut.intParser),
            row(-1231231.123, """{"type":"int"}""", sut.intParser),
            row("12312", """{"type":"int"}""", sut.intParser),
            // long parser
            row(true, """{"type":"long"}""", sut.longParser),
            row("12312", """{"type":"long"}""", sut.longParser),
            // float parser
            row(4321, """{"type":"float"}""", sut.floatParser),
            row("1231", """{"type":"float"}""", sut.floatParser),
            row("1231.2", """{"type":"float"}""", sut.floatParser),
            // double parser
            row(4321, """{"type": "double"}""", sut.doubleParser),
            row("1231", """{"type": "double"}""", sut.doubleParser),
            row("1231.2", """{"type": "double"}""", sut.doubleParser),
            // null parser
            row("null", """{"type": "null"}""", sut.nullParser),
            row("", """{"type": "null"}""", sut.nullParser),
            // boolean parser
            row("1", """{"type": "boolean"}""", sut.booleanParser),
            row("", """{"type": "boolean"}""", sut.booleanParser),
            row(1, """{"type": "boolean"}""", sut.booleanParser),
            row(0, """{"type": "boolean"}""", sut.booleanParser),
            // string parser
            row(4321, """{"type": "string"}""", sut.stringParser),
            row(true, """{"type": "string"}""", sut.stringParser),
            row(null, """{"type": "string"}""", sut.stringParser),
        ).forAll { testValue, stringSchema, parser ->
            // arrange
            val schema = Schema.Parser().parse(stringSchema)
            // act
            val res = parser.parse(testValue, schema)
            // assert
            res.shouldBeLeft().should { it.shouldBeInstanceOf<JsonInvalidFieldException>() }
        }
    }

    "enumParser  happy path" {
        // arrange
        val testValue = "SPADES"
        val parser = sut.enumParser
        val schema = Schema.Parser().parse("""{"type": "enum", "name": "Suit", "symbols" : ["SPADES", "HEARTS", "DIAMONDS", "CLUBS"]}""")
        // act
        val res = parser.parse(testValue, schema)
        // assert
        res shouldBeRight GenericData.EnumSymbol(schema, testValue)
    }

    "enumParser parse invalid field" {
        table(
            headers("test value"),
            row("spades"),
            row("HEARTs"),
            row(null),
        ).forAll { testValue ->
            // arrange
            val parser = sut.enumParser
            val schema = Schema.Parser().parse(
                """{
            "type": "enum",
            "name": "Suit",
            "symbols" : ["SPADES", "HEARTS", "DIAMONDS", "CLUBS"]
        }
                """.trimIndent()
            )
            // act
            val res = parser.parse(testValue, schema)
            // assert
            res.shouldBeLeft().should { it.shouldBeInstanceOf<JsonInvalidFieldException>() }
        }
    }
})
