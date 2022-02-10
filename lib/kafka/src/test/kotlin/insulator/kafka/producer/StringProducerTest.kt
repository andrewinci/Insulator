package insulator.kafka.producer

import insulator.kafka.model.Cluster
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.apache.kafka.clients.producer.Producer as KafkaProducer

class StringProducerTest : StringSpec({

    "factory happy path" {
        // arrange
        val mockCluster = Cluster.empty().copy(
            endpoint = "localhost:9090"
        )
        // act
        val sut = stringProducer(mockCluster)
        // assert
        sut shouldNotBe null
    }

    "send happy path" {
        // arrange
        val kafkaProducer = mockk<KafkaProducer<String, String>>(relaxed = true) {
            every { send(any()) } returns mockk()
        }
        val sut = StringProducer { kafkaProducer }
        // act
        val res = sut.send("topic", "key", "value", null)
        sut.close()
        // assert
        res shouldBeRight {}
        verify(exactly = 1) { kafkaProducer.close() }
    }

    "validate always succeed" {
        // arrange
        val sut = StringProducer(mockk())
        // act
        val res = sut.validate("random string ] ; {", "any-topic", null)
        // assert
        res shouldBeRight Unit
    }

    "send return an error if the underlying operation fails" {
        // arrange
        val error = Throwable("error message")
        val sut = StringProducer {
            mockk {
                every { send(any()) } throws error
            }
        }
        // act
        val res = sut.send("topic", "key", "value", null)
        // assert
        res shouldBeLeft error
    }
})
