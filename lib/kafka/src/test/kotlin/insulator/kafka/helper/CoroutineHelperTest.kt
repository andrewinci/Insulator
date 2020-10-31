package insulator.kafka.helper

import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.StringSpec
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.apache.kafka.common.KafkaFuture
import org.apache.kafka.common.internals.KafkaFutureImpl
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

@ExperimentalTime
class CoroutineHelperTest : StringSpec({

    "Completed kafka future to coroutine" {
        // arrange
        val testData = "test"
        val kafkaFuture = KafkaFuture.completedFuture(testData)
        // act
        val res = kafkaFuture.toSuspendCoroutine()
        // assert
        res shouldBeRight testData
    }

    "Exceptionally complete kafka future to coroutine exceptional completion" {
        // arrange
        val testData = Exception("Error")
        val kafkaFuture = KafkaFutureImpl<String>()
            .also { it.completeExceptionally(testData) }
        // act
        val res = kafkaFuture.toSuspendCoroutine()
        // assert
        res shouldBeLeft testData
    }

    "Complete in the future" {
        // arrange
        val testData = "test"
        val kafkaFuture = KafkaFutureImpl<String>()
        launch { delay(2.seconds); kafkaFuture.complete(testData) }
        // act
        val res = kafkaFuture.toSuspendCoroutine()
        // assert
        res shouldBeRight testData
    }

    "Exceptionally complete in the future" {
        // arrange
        val testData = Exception("Error")
        val kafkaFuture = KafkaFutureImpl<String>()
        launch { delay(2.seconds); kafkaFuture.completeExceptionally(testData) }
        // act
        val res = kafkaFuture.toSuspendCoroutine()
        // assert
        res shouldBeLeft testData
    }
})
