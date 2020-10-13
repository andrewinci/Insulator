package insulator.viewmodel.main.schemaregistry

import arrow.core.left
import arrow.core.right
import helper.FxContext
import insulator.lib.helpers.runOnFXThread
import insulator.lib.jsonhelper.JsonFormatter
import insulator.lib.kafka.SchemaRegistry
import insulator.lib.kafka.model.Schema
import insulator.lib.kafka.model.Subject
import insulator.viewmodel.main.MainViewModel
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.reflect.KClass

class ListSchemaViewModelTest : StringSpec({

    "Happy path" {
        TestFixture().use {
            // arrange
            val sut = ListSchemaViewModel()
            // act
            val schemas = sut.filteredSchemas
            // assert
            it.waitFXThread()
            schemas.size shouldBe 2
            sut.error.value shouldBe null
        }
    }

    "Show an error if unable to retrieve the configuration" {
        TestFixture().use {
            // arrange
            every { it.schemaRegistry.second.getAllSubjects() } returns Throwable(it.errorMessage).left()
            val sut = ListSchemaViewModel()
            // act
            val schemas = sut.filteredSchemas
            // assert
            schemas.size shouldBe 0
            sut.error.value!!.message shouldBe it.errorMessage
        }
    }

    "Happy path show schema" {
        TestFixture().use {
            // arrange
            val mockMainViewModel = mockk<MainViewModel>(relaxed = true)
            it.configureFxDi(mockMainViewModel)
            val sut = ListSchemaViewModel()
            sut.selectedSchema.value = it.targetSubject
            // act
            sut.runOnFXThread { showSchema() }
            // assert
            it.waitFXThread()
            verify(exactly = 1) { it.schemaRegistry.second.getSubject(it.targetSubject) }
            verify(exactly = 1) { mockMainViewModel.showTab(any(), any()) }
            sut.error.value shouldBe null
        }
    }

    "Show an error if unable to retrieve the schema" {
        TestFixture().use {
            // arrange
            every { it.schemaRegistry.second.getSubject(any()) } returns Throwable(it.errorMessage).left()
            val sut = ListSchemaViewModel()
            sut.selectedSchema.value = it.targetSubject
            // act
            sut.runOnFXThread { showSchema() }
            // assert
            it.waitFXThread()
            verify(exactly = 1) { it.schemaRegistry.second.getSubject(it.targetSubject) }
            sut.error.value shouldBe LoadSchemaError(it.errorMessage)
        }
    }
})

private class TestFixture() : FxContext() {

    val errorMessage = "Example error"
    val targetSubject = "subject1"

    var jsonFormatter: Pair<KClass<JsonFormatter>, JsonFormatter> = JsonFormatter::class to mockk(relaxed = true) {
        every { formatJsonString(any()) } returns listOf(insulator.lib.jsonhelper.Token.COLON).right()
    }
    var schemaRegistry: Pair<KClass<SchemaRegistry>, SchemaRegistry> = SchemaRegistry::class to mockk {
        every { getAllSubjects() } returns listOf(targetSubject, "subject2").right()
        every { getSubject(any()) } returns Subject("*", listOf(Schema("{}", 1, 3))).right()
    }

    init {
        addToDI(schemaRegistry, jsonFormatter)
    }
}
