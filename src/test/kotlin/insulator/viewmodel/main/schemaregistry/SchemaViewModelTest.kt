package insulator.viewmodel.main.schemaregistry

import arrow.core.right
import helper.configureDi
import helper.configureFXFramework
import insulator.lib.jsonhelper.JsonFormatter
import insulator.lib.jsonhelper.Token
import insulator.lib.kafka.SchemaRegistry
import insulator.lib.kafka.model.Schema
import insulator.lib.kafka.model.Subject
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class SchemaViewModelTest : FunSpec({

    lateinit var mockSchemaRegistry: SchemaRegistry

    test("happy path delete") {
        // arrange
        val subject = Subject(subject = "subject", schemas = listOf(Schema("{}", 1)))
        val sut = SchemaViewModel(subject)
        // act
        sut.delete()
        // assert
        verify(exactly = 1) { mockSchemaRegistry.deleteSubject("subject") }
        sut.error.value shouldBe null
    }

    beforeTest {
        configureFXFramework()
        mockSchemaRegistry = mockk(relaxed = true)
        configureDi(
            SchemaRegistry::class to mockSchemaRegistry,
            JsonFormatter::class to mockk<JsonFormatter> {
                every { formatJsonString(any()) } returns listOf(Token.COLON).right()
            }
        )
    }
})
