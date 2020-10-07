package insulator.lib.jsonhelper.jsontoavro.fieldparser

import arrow.core.left
import arrow.core.right
import insulator.lib.jsonhelper.jsontoavro.JsonFieldParsingException
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import org.apache.avro.Schema

class UnionParserTest : FunSpec({
    val schema = Schema.Parser()
        .parse(""" {"type": "record", "name": "unionTest", "fields": [{ "name": "t", "type": [ "null", "string"] }] } """)
        .fields.first().schema()

    test("happy path right union") {
        // arrange
        val sut = UnionParser(
            mockk {
                every { parseField(any(), any()) } returns "".right()
            }
        )
        // act
        val res = sut.parse("test-string", schema)
        // assert
        res shouldBeRight {}
    }

    test("happy path left union") {
        // arrange
        val sut = UnionParser(
            mockk {
                every { parseField(any(), any()) } returns null.right()
            }
        )
        // act
        val res = sut.parse(null, schema)
        // assert
        res shouldBeRight {}
    }

    test("join errors if value doesn't match any type in the union") {
        // arrange
        val sut = UnionParser(
            mockk {
                every { parseField(any(), any()) } returnsMany
                    listOf("error1", "error2").map { JsonFieldParsingException(it).left() }
            }
        )
        // act
        val res = sut.parse(1, schema)
        // assert
        res shouldBeLeft {
            it.shouldBeInstanceOf<JsonFieldParsingException>()
            it.message shouldBe "error1\nerror2"
        }
    }
})
