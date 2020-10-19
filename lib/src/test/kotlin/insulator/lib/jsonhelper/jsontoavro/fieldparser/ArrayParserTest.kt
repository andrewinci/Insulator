package insulator.lib.jsonhelper.jsontoavro.fieldparser

import arrow.core.right
import insulator.lib.jsonhelper.jsontoavro.JsonInvalidFieldException
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import org.apache.avro.Schema

class ArrayParserTest : StringSpec({

    val sampleSchema = Schema.Parser().parse(
        """
            {
                "type": "array",
                "items" : "int"
            }
        """.trimIndent()
    )

    "happy path" {
        // arrange
        val field = ArrayList<Int>(listOf(1, 1, 1, 1, 1))
        val sut = ArrayParser(
            mockk {
                every { parseField(any(), any()) } returns 1.right()
            }
        )
        // act
        val res = sut.parse(field, sampleSchema)
        // assert
        res shouldBeRight field.toList()
    }

    "return left if not an array" {
        // arrange
        val field = 123
        val sut = ArrayParser(mockk())
        // act
        val res = sut.parse(field, sampleSchema)
        // assert
        res shouldBeLeft {
            it.shouldBeInstanceOf<JsonInvalidFieldException>()
        }
    }

    "parse empty arrays successfully" {
        // arrange
        val field = ArrayList<Int>()
        val sut = ArrayParser(mockk())
        // act
        val res = sut.parse(field, sampleSchema)
        // assert
        res shouldBeRight emptyList<Any>()
    }
})
