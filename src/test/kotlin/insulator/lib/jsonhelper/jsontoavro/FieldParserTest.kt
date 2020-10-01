package insulator.lib.jsonhelper.jsontoavro

import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.core.spec.style.FunSpec
import io.mockk.mockk
import org.apache.avro.Schema

class FieldParserTest : FunSpec({

    test("Parse unsupported return left") {
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
        res shouldBeLeft {}
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
