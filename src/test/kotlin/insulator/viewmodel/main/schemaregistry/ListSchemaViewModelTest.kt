package insulator.viewmodel.main.schemaregistry

import arrow.core.left
import arrow.core.right
import helper.cleanupFXFramework
import helper.configureDi
import helper.configureFXFramework
import helper.configureScopeDi
import helper.waitFXThread
import insulator.lib.helpers.runOnFXThread
import insulator.lib.jsonhelper.JsonFormatter
import insulator.lib.jsonhelper.Token
import insulator.lib.kafka.SchemaRegistry
import insulator.lib.kafka.model.Schema
import insulator.lib.kafka.model.Subject
import insulator.viewmodel.main.MainViewModel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import tornadofx.set
import kotlin.reflect.KClass

class ListSchemaViewModelTest : FunSpec({

    val errorMessage = "Example error"
    val targetSubject = "subject1"
    lateinit var jsonFormatter: Pair<KClass<JsonFormatter>, JsonFormatter>
    lateinit var schemaRegistry: Pair<KClass<SchemaRegistry>, SchemaRegistry>

    test("Happy path") {
        // arrange
        val sut = ListSchemaViewModel()
        // act
        val schemas = sut.filteredSchemas
        // assert
        waitFXThread()
        schemas.size shouldBe 2
        sut.error.value shouldBe null
    }

    test("Show an error if unable to retrieve the configuration") {
        // arrange
        every { schemaRegistry.second.getAllSubjects() } returns Throwable(errorMessage).left()
        val sut = ListSchemaViewModel()
        // act
        val schemas = sut.filteredSchemas
        // assert
        schemas.size shouldBe 0
        sut.error.value!!.message shouldBe errorMessage
    }

    test("Happy path show schema") {
        // arrange
        val mockMainViewModel = mockk<MainViewModel>(relaxed = true)
        configureScopeDi(mockMainViewModel)
        val sut = ListSchemaViewModel()
        sut.selectedSchema.value = targetSubject
        // act
        sut.runOnFXThread { showSchema() }
        // assert
        waitFXThread()
        verify(exactly = 1) { schemaRegistry.second.getSubject(targetSubject) }
        verify(exactly = 1) { mockMainViewModel.showTab(any(), any()) }
        sut.error.value shouldBe null
    }

    test("Show an error if unable to retrieve the schema") {
        // arrange
        every { schemaRegistry.second.getSubject(any()) } returns Throwable(errorMessage).left()
        val sut = ListSchemaViewModel()
        sut.selectedSchema.value = targetSubject
        // act
        sut.runOnFXThread { showSchema() }
        // assert
        waitFXThread()
        verify(exactly = 1) { schemaRegistry.second.getSubject(targetSubject) }
        sut.error.value shouldBe LoadSchemaError(errorMessage)
    }

    beforeTest {
        configureFXFramework()
        jsonFormatter = JsonFormatter::class to mockk(relaxed = true) {
            every { formatJsonString(any()) } returns listOf(Token.COLON).right()
        }
        schemaRegistry = SchemaRegistry::class to mockk {
            every { getAllSubjects() } returns listOf(targetSubject, "subject2").right()
            every { getSubject(any()) } returns Subject("*", listOf(Schema("{}", 1, 3))).right()
        }
        configureDi(schemaRegistry, jsonFormatter)
    }

    afterTest {
        cleanupFXFramework()
    }
})
