package insulator.viewmodel.main.topic

import insulator.kafka.model.Record
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.util.Base64

class RecordViewModelTest : StringSpec({

    "parsed headers test" {
        // arrange
        val base64 = { s: String -> Base64.getDecoder().decode(s) }
        val headers = mapOf("header1" to listOf(base64("TWFu"), base64("c3VyZS4=")))
        val testRecord = Record(key = "key", value = "value", timestamp = 123, headers = headers, partition = 0, offset = 1)
        // act
        val recordViewModel = RecordViewModel(testRecord)
        // assert
        recordViewModel.formattedHeadersProperty.value shouldBe "header1: TWFu\nheader1: c3VyZS4="
    }
})
