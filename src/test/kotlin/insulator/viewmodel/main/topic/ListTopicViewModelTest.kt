package insulator.viewmodel.main.topic

import arrow.core.left
import insulator.lib.kafka.AdminApi
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.testfx.api.FxToolkit
import tornadofx.* // ktlint-disable no-wildcard-imports
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass

class ListTopicViewModelTest : FunSpec({

    val errorMessage = "Example error"

    test("topicList") {
        FxToolkit.registerPrimaryStage()
        // arrange
        val sut = ListTopicViewModel()
        // act
        val res = sut.topicList
        // assert
        sut.refresh().get()
        res.size shouldBe 0
        sut.error.value!!.message shouldBe errorMessage
    }

    beforeTest {
        FX.dicontainer = object : DIContainer {
            @Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
            override fun <T : Any> getInstance(type: KClass<T>): T {
                return when (type.java) {
                    AdminApi::class.java -> mockk<AdminApi> {
                        every { listTopics() } returns CompletableFuture.completedFuture(Throwable(errorMessage).left())
                    }
                    else -> throw IllegalArgumentException("Missing dependency")
                } as T
            }
        }
    }
})
