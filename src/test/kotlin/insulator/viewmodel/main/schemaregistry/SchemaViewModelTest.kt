package insulator.viewmodel.main.schemaregistry

import arrow.core.right
import helper.FxContext
import insulator.lib.jsonhelper.JsonFormatter
import insulator.lib.jsonhelper.Token
import insulator.lib.kafka.SchemaRegistry
import insulator.lib.kafka.model.Schema
import insulator.lib.kafka.model.Subject
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class SchemaViewModelTest : StringSpec({

    "happy path delete" {
        TestContext().use {
            // arrange
            val subject = Subject(name = it.targetSubject, schemas = listOf(Schema("{}", 1, 4)))
            val sut = SchemaViewModel(subject)
            // act
            sut.delete()
            // assert
            verify(exactly = 1) { it.mockSchemaRegistry.deleteSubject(it.targetSubject) }
            sut.error.value shouldBe null
        }
    }
})

private class TestContext : FxContext() {

    var mockSchemaRegistry = mockk<SchemaRegistry>(relaxed = true) {
        every { getAllSubjects() } returns listOf(targetSubject).right()
    }
    val targetSubject = "subject"

    init {
        addToDI(
            SchemaRegistry::class to mockSchemaRegistry,
            JsonFormatter::class to mockk<JsonFormatter> {
                every { formatJsonString(any()) } returns listOf(Token.COLON).right()
            }
        )
    }
}
