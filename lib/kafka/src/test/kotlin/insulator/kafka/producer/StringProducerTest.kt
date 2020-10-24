package insulator.kafka.producer

import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.StringSpec
import io.mockk.every
import io.mockk.mockk

class StringProducerTest : StringSpec({

    "validate always succeed" {
        // arrange
        val sut = StringProducer(mockk())
        // act
        val res = sut.validate("random string ] ; {", "any-topic")
        // assert
        res shouldBeRight Unit
    }

    "send return an error if the underlying operation fails" {
        // arrange
        val error = Throwable("error message")
        val sut = StringProducer(
            mockk {
                every { send(any()) } throws error
            }
        )
        // act
        val res = sut.send("topic", "key", "value")
        // assert
        res shouldBeLeft error
    }
})
