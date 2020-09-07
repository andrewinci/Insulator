package insulator.viewmodel.main.topic

import arrow.core.Tuple3
import arrow.core.right
import insulator.lib.kafka.AdminApi
import insulator.lib.kafka.Consumer
import insulator.lib.kafka.model.Topic
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.delay
import tornadofx.* // ktlint-disable no-wildcard-imports
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass

class TopicViewModelTest : FunSpec({

    val topicName = "topic-name"

    test("clear remove all records from the list") {
        // arrange
        val sut = TopicViewModel(topicName)
        sut.records.add(mockk())
        // act
        sut.clear()
        // assert
        sut.records.size shouldBe 0
    }

    test("stop an already stopped consumer is ignored") {
        // arrange
        val sut = TopicViewModel(topicName)
        // act
        sut.stop()
    }

    test("delete call the deleteTopic function from lib with the topic name") {
        // arrange
        val sut = TopicViewModel(topicName)
        // act
        sut.delete()
        // assert
    }

    test("consume") {
        // arrange
        val sut = TopicViewModel(topicName)
        // act
        sut.consume()
        delay(1000)
        // assert
        sut.records.count() shouldBe 3
    }

    beforeTest {
        FX.dicontainer = object : DIContainer {
            @Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
            override fun <T : Any> getInstance(type: KClass<T>): T {
                return when (type.java) {
                    AdminApi::class.java -> mockk<AdminApi> {
                        every { describeTopic(any()) } returns CompletableFuture.completedFuture(Topic("Topic name").right())
                        every { deleteTopic(any()) } returns CompletableFuture.completedFuture(null.right())
                    }
                    Consumer::class.java -> mockk<Consumer> {
                        every { start(any(), any(), any(), any()) } answers {
                            lastArg<(List<Tuple3<String?, String, Long>>) -> Unit>()(listOf(Tuple3("1", "2", 3L)))
                            lastArg<(List<Tuple3<String?, String, Long>>) -> Unit>()(listOf(Tuple3("1", "2", 3L)))
                            lastArg<(List<Tuple3<String?, String, Long>>) -> Unit>()(listOf(Tuple3("1", "2", 3L)))
                        }
                        every { stop() } just runs
                        every { isRunning() } returns false
                    }
                    else -> throw IllegalArgumentException("Missing dependency")
                } as T
            }
        }
    }
})
