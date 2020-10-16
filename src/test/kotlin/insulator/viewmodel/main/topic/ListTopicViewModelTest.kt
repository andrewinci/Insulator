package insulator.viewmodel.main.topic

import arrow.core.left
import arrow.core.right
import helper.FxContext
import insulator.lib.kafka.AdminApi
import io.kotest.assertions.timing.eventually
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import java.util.concurrent.CompletableFuture
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

@ExperimentalTime
class ListTopicViewModelTest : StringSpec({

    val errorMessage = "Example error"

    "topicList error on loading the list of topics" {
        FxContext().use {
            // arrange
            it.addToDI(
                AdminApi::class to mockk<AdminApi> {
                    coEvery { listTopics() } returns Throwable(errorMessage).left()
                }
            )
            val sut = ListTopicViewModel()
            // act
            val res = sut.filteredTopicsProperty
            // assert
            sut.refresh()
            eventually(1.seconds) {
                res.size shouldBe 0
                sut.error.value!!.message shouldBe errorMessage
            }
        }
    }

    "happy path" {
        FxContext().use {
            // arrange
            it.addToDI(
                AdminApi::class to mockk<AdminApi> {
                    coEvery { listTopics() } returns listOf("tppic1", "topic2").right()
                }
            )
            val sut = ListTopicViewModel()
            // act
            val res = sut.filteredTopicsProperty
            // assert
            sut.refresh()
            eventually(1.seconds) {
                res.size shouldBe 2
                sut.error.value shouldBe null
            }
        }
    }
})
