package insulator.viewmodel.main.topic

import arrow.core.Tuple3
import insulator.kafka.consumer.Consumer
import insulator.kafka.model.Topic
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.delay

class ConsumerViewModelTest : StringSpec({

    "clear remove all records from the list" {
        // arrange
        val sut = ConsumerViewModel(Topic.empty(), mockk())
        sut.records.add(mockk())
        // act
        sut.clearRecords()
        // assert
        sut.records.size shouldBe 0
    }

    "stop an already stopped consumer is ignored" {
        with(ConsumerViewModelTestContext()) {
            // arrange
            val sut = ConsumerViewModel(Topic.empty(), mockConsumer)
            // act
            sut.stop()
        }
    }

    "consume" {
        with(ConsumerViewModelTestContext()) {
            // arrange
            val sut = ConsumerViewModel(Topic.empty(), mockConsumer)
            // act
            sut.consume()
            delay(1000)
            // assert
            sut.records.count() shouldBe 3
        }
    }
})

private class ConsumerViewModelTestContext {
    val mockConsumer = mockk<Consumer> {
        coEvery { start(any(), any(), any(), any()) } answers {
            arg<(List<Tuple3<String?, String, Long>>) -> Unit>(3)(listOf(Tuple3("1", "2", 3L)))
            arg<(List<Tuple3<String?, String, Long>>) -> Unit>(3)(listOf(Tuple3("1", "2", 3L)))
            arg<(List<Tuple3<String?, String, Long>>) -> Unit>(3)(listOf(Tuple3("1", "2", 3L)))
        }
        coEvery { stop() } just runs
        every { isRunning() } returns false
    }
}
