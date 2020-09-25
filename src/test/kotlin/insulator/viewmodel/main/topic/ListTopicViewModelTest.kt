package insulator.viewmodel.main.topic

import arrow.core.left
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
        val sut = ListTopicViewModel()
        // act
        val res = sut.topicList
        // assert
        sut.refresh().get()
        res.size shouldBe 0
        sut.error.value!!.message shouldBe errorMessage
    }

    beforeTest {
        configureFXFramework()
        configureDi(
            AdminApi::class to mockk<AdminApi> {
                every { listTopics() } returns CompletableFuture.completedFuture(Throwable(errorMessage).left())
            }
        )
    }
    afterTest {
        cleanupFXFramework()
    }
})
