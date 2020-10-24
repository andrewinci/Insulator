package insulator.viewmodel.main.schemaregistry

import arrow.core.left
import arrow.core.right
import helper.FxContext
import insulator.helper.dispatch
import insulator.kafka.SchemaRegistry
import insulator.kafka.model.Schema
import insulator.kafka.model.Subject
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk

class ListSchemaViewModelTest : StringSpec({

    "Happy path" {
        ListSchemaViewModelTestFixture().use {
            // arrange
            val sut = ListSchemaViewModel(it.cluster, it.schemaRegistry, mockk(), mockk())
            // act
            val schemas = sut.filteredSchemasProperty
            // assert
            it.waitFXThread()
            schemas.size shouldBe 2
            sut.error.value shouldBe null
        }
    }

    "Show an error if unable to retrieve the configuration" {
        ListSchemaViewModelTestFixture().use {
            // arrange
            every { it.schemaRegistry.getAllSubjects() } returns Throwable(it.errorMessage).left()
            val sut = ListSchemaViewModel(it.cluster, it.schemaRegistry, mockk(), mockk())
            // act
            val schemas = sut.filteredSchemasProperty
            // assert
            schemas.size shouldBe 0
            sut.error.value!!.message shouldBe it.errorMessage
        }
    }
// todo:Move to tabViewModel
//    "Happy path show schema" {
//        ListSchemaViewModelTestFixture().use {
//            // arrange
//            val mockMainViewModel = mockk<MainViewModel>(relaxed = true)
//            it.configureFxDi(mockMainViewModel)
//            val sut = ListSchemaViewModel()
//            sut.selectedSchemaProperty.value = it.targetSubject
//            // act
//            sut.dispatch { showSchema() }
//            // assert
//            it.waitFXThread()
//            coVerify(exactly = 1) { it.schemaRegistry.second.getSubject(it.targetSubject) }
//            verify(exactly = 1) { mockMainViewModel.showTab(any(), any()) }
//            sut.error.value shouldBe null
//        }
//    }

    "Show an error if unable to retrieve the schema" {
        ListSchemaViewModelTestFixture().use {
            // arrange
            coEvery { it.schemaRegistry.getSubject(any()) } returns Throwable(it.errorMessage).left()
            val sut = ListSchemaViewModel(it.cluster, it.schemaRegistry, mockk(), mockk())
            sut.selectedSchemaProperty.value = it.targetSubject
            // act
            sut.dispatch { showSchema() }
            // assert
            it.waitFXThread()
            coVerify(exactly = 1) { it.schemaRegistry.getSubject(it.targetSubject) }
            sut.error.value shouldBe LoadSchemaError(it.errorMessage)
        }
    }
})

private class ListSchemaViewModelTestFixture : FxContext() {

    val errorMessage = "Example error"
    val targetSubject = "subject1"

    var schemaRegistry = mockk<SchemaRegistry> {
        every { getAllSubjects() } returns listOf(targetSubject, "subject2").right()
        coEvery { getSubject(any()) } returns Subject("*", listOf(Schema("{}", 1, 3))).right()
    }
}
