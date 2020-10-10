package insulator.viewmodel.main.topic

import arrow.core.left
import arrow.core.right
import helper.cleanupFXFramework
import helper.configureDi
import helper.configureFXFramework
import insulator.lib.kafka.AdminApi
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.util.concurrent.CompletableFuture

class ListTopicViewModelTest : FunSpec({

    val errorMessage = "Example error"

    test("topicList error on loading the list of topics") {
        // arrange
        configureDi(
            AdminApi::class to mockk<AdminApi> {
                every { listTopics() } returns CompletableFuture.completedFuture(Throwable(errorMessage).left())
            }
        )
        val sut = ListTopicViewModel()
        // act
        val res = sut.filteredTopics
        // assert
        sut.refresh().get() // force and wait refresh
        res.size shouldBe 0
        sut.error.value!!.message shouldBe errorMessage
    }

    test("happy path") {
        // arrange
        configureDi(
            AdminApi::class to mockk<AdminApi> {
                every { listTopics() } returns CompletableFuture.completedFuture(listOf("tppic1", "topic2").right())
            }
        )
        val sut = ListTopicViewModel()
        // act
        val res = sut.filteredTopics
        // assert
        sut.refresh().get() // force and wait refresh
        res.size shouldBe 2
        sut.error.value shouldBe null
    }

    beforeTest {
        configureFXFramework()
    }
    afterTest {
        cleanupFXFramework()
    }
})
