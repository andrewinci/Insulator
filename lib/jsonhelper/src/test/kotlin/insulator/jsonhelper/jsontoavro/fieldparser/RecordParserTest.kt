package insulator.jsonhelper.jsontoavro.fieldparser

import arrow.core.right
import insulator.jsonhelper.jsontoavro.JsonInvalidFieldException
import insulator.jsonhelper.jsontoavro.JsonMissingFieldException
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import org.apache.avro.Schema

class RecordParserTest : StringSpec({
    val schema = Schema.Parser().parse(
        """
            {
                "type": "record",
                "name": "recordName",
                "fields": [
                    { "name": "field1", "type": "string" }
                ]
            }
        """.trimIndent()
    )

    "happy path" {
        // arrange
        val sut = RecordParser(
            mockk {
                every { parseField(any(), any()) } returns "".right()
            }
        )
        // act
        val res = sut.parse(mapOf("field1" to "fieldValue"), schema)
        // assert
        res shouldBeRight {}
    }

    "invalid record" {
        // arrange
        val sut = RecordParser(
            mockk {
                every { parseField(any(), any()) } returns "".right()
            }
        )
        // act
        val res = sut.parse("", schema)
        // assert
        res shouldBeLeft {
            it.shouldBeInstanceOf<JsonInvalidFieldException>()
        }
    }

    "record with a missing field" {
        // arrange
        val sut = RecordParser(
            mockk {
                every { parseField(any(), any()) } returns "".right()
            }
        )
        // act
        val res = sut.parse(emptyMap<String, String>(), schema)
        // assert
        res shouldBeLeft {
            it.shouldBeInstanceOf<JsonMissingFieldException>()
        }
    }
})
