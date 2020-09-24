package insulator.lib.jsonhelper

import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.apache.avro.generic.GenericRecord

class NestedJsonToAvroConverterTest : FunSpec({

    test("Convert unnested json with primitive types only to avro") {
        // arrange
        val schema =
            """
            {
                "type": "record",
                "name": "value_test_schema",
                "namespace": "com.mycorp.mynamespace",
                "fields": [
                    {
                        "name": "testRecord",
                        "type": {
                            "type": "record",
                            "name": "nested_record",
                            "namespace": "com.mycorp.mynamespace",
                            "fields": [
                                {
                                    "name": "testString",
                                    "type": "string"
                                }
                            ]
                        }
                    }
                ]
            }
            """.trimIndent()
        val sampleJson =
            """{ "testRecord": { "testString": "string" }}""".trimIndent()
        val sut = JsonToAvroConverter()
        // act
        val result = sut.convert(sampleJson, schema)
        // assert
        result shouldBeRight {
            (it.get("testRecord") as GenericRecord).get("testString") shouldBe "string"
        }
    }
})
