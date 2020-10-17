package insulator.viewmodel.main.topic

import arrow.core.left
import arrow.core.right
import helper.FxContext
import insulator.lib.kafka.AdminApi
import insulator.lib.kafka.Consumer
import insulator.lib.kafka.model.Topic
import insulator.viewmodel.main.MainViewModel
import io.kotest.assertions.timing.eventually
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
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
                    coEvery { listTopics() } returns listOf("topic1", "topic2").right()
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

    "show topic is showing the selected topic" {
        ListTopicViewModelTestContext().use {
            // arrange
            val sut = ListTopicViewModel()
            sut.selectedItemProperty.set(it.topicName)
            // act
            val newView = sut.showTopic()
            // assert
            newView?.title shouldBe it.topicName
        }
    }

    "refresh list of topic when a topic view is closed" {
        ListTopicViewModelTestContext().use {
            // arrange
            val sut = ListTopicViewModel()
            sut.selectedItemProperty.set("Example-topic")
            // act
            val newView = sut.showTopic()
            newView!!.close()
            // assert
            coVerify(exactly = 2) { it.adminMock.listTopics() }
        }
    }
})

class ListTopicViewModelTestContext : FxContext() {

    val topicName = "Example-topic"
    val adminMock = mockk<AdminApi> {
        coEvery { describeTopic(any()) } returns Topic(topicName).right()
        coEvery { listTopics() } returns listOf("topic1", "topic2").right()
    }

    init {
        addToDI(
            AdminApi::class to adminMock,
            Consumer::class to mockk<Consumer>(),
            MainViewModel::class to mockk<MainViewModel>(relaxed = true)
        )
    }
}
