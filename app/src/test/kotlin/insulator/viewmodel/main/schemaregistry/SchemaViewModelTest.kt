package insulator.viewmodel.main.schemaregistry

import arrow.core.right
import helper.FxContext
import insulator.jsonhelper.JsonFormatter
import insulator.jsonhelper.Token
import insulator.kafka.SchemaRegistry
import insulator.kafka.model.Schema
import insulator.kafka.model.Subject
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class SchemaViewModelTest : StringSpec({

    "happy path delete" {
        SchemaViewModelTestFixture().use {
            // arrange
            val subject = Subject(name = it.targetSubject, schemas = listOf(Schema("{}", 1, 4)))
            val sut = SchemaViewModel(it.cluster, subject, it.mockSchemaRegistry)
            // act
            sut.delete()
            // assert
            verify(exactly = 1) { it.mockSchemaRegistry.deleteSubject(it.targetSubject) }
            sut.error.value shouldBe null
        }
    }

    "happy path delete schema version" {
        SchemaViewModelTestFixture().use {
            // arrange
            val subject = Subject(name = it.targetSubject, schemas = listOf(Schema("{}", 1, 4)))
            val sut = SchemaViewModel(it.cluster, subject, it.mockSchemaRegistry)
            // act
            sut.deleteSchemaVersion()
            // assert
            verify(exactly = 1) { it.mockSchemaRegistry.deleteSchemaVersion(it.targetSubject, subject.schemas[0].version) }
            sut.error.value shouldBe null
        }
    }
})

private class SchemaViewModelTestFixture : FxContext() {
    val mockJsonFormatter = mockk<JsonFormatter> {
        every { formatJsonString(any()) } returns listOf(Token.COLON).right()
    }
    var mockSchemaRegistry = mockk<SchemaRegistry>(relaxed = true) {
        every { getAllSubjects() } returns listOf(targetSubject).right()
    }
    val targetSubject = "subject"
}
