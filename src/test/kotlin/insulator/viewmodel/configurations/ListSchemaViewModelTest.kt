package insulator.viewmodel.configurations

import arrow.core.left
import helper.configureDi
import helper.configureFXFramework
import insulator.lib.kafka.SchemaRegistry
import insulator.viewmodel.main.schemaregistry.ListSchemaViewModel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class ListSchemaViewModelTest : FunSpec({

    val errorMessage = "Example error"

    test("Show an error if unable to retrieve the configuration") {
        // arrange
        val sut = ListSchemaViewModel()
        // act
        val clusters = sut.listSchema
        // assert
        clusters.size shouldBe 0
        sut.error.value!!.message shouldBe errorMessage
    }

    beforeTest {
        configureFXFramework()
        configureDi(
            SchemaRegistry::class to mockk<SchemaRegistry> {
                every { getAllSubjects() } returns Throwable(errorMessage).left()
            }
        )
    }
})
