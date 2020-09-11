package insulator.viewmodel.configurations

import arrow.core.left
import insulator.lib.kafka.SchemaRegistry
import insulator.viewmodel.main.schemaregistry.ListSchemaViewModel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import tornadofx.* // ktlint-disable no-wildcard-imports
import kotlin.reflect.KClass

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
        FX.dicontainer = object : DIContainer {
            @Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
            override fun <T : Any> getInstance(type: KClass<T>): T = when (type.java) {
                SchemaRegistry::class.java -> mockk<SchemaRegistry> {
                    every { getAllSubjects() } returns Throwable(errorMessage).left()
                }
                else -> throw IllegalArgumentException("Missing dependency")
            } as T
        }
    }
})
