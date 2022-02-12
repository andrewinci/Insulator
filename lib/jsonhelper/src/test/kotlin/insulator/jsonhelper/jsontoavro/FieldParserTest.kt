package insulator.jsonhelper.jsontoavro

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.core.spec.style.StringSpec
import io.mockk.mockk
import org.apache.avro.Schema

class FieldParserTest : StringSpec({

    "Parse unsupported return left" {
        // arrange
        val testFieldName = "testFieldName"
        val sut = FieldParser(mockk(relaxed = true), mockk(relaxed = true))
        val schema = Schema.Parser().parse(
            schemaTemplate(
                """{"name":"testFieldName", "type": { "type": "map", "values" : "long" }
            |}""".trimMargin()
            )
        )
        // act
        val res = sut.parseField("""{"$testFieldName": "sample"}""", schema.fields[0].schema())
        // assert
        res.shouldBeLeft()
    }
})
