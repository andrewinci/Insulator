package insulator.viewmodel.main.topic

import arrow.core.left
import arrow.core.right
import helper.cleanupFXFramework
import helper.configureDi
import helper.configureFXFramework
import insulator.lib.configuration.model.Cluster
import insulator.lib.configuration.model.SchemaRegistryConfiguration
import insulator.lib.jsonhelper.jsontoavro.JsonToAvroException
import insulator.lib.kafka.AvroProducer
import insulator.lib.kafka.Producer
import insulator.lib.kafka.StringProducer
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk

class ProducerViewModelTest : FunSpec({

    val errorMessage = "Example error"

    test("string producer show a validation error if the message is invalid") {
        // arrange
        configureDi(
            Cluster::class to Cluster.empty(),
            StringProducer::class to mockk<Producer> {
                every { validate(any(), any()) } returns JsonToAvroException(errorMessage).left()
            }
        )
        val sut = ProducerViewModel("test-topic")
        // act
        sut.valueProperty.set("test")
        // assert
        sut.validationErrorProperty.value shouldBe errorMessage
        sut.canSendProperty.value shouldBe false
    }

    test("send message without value fails") {
        // arrange
        configureDi(
            Cluster::class to Cluster.empty(),
            StringProducer::class to mockk<Producer> {
                every { validate(any(), any()) } returns Unit.right()
            }
        )
        val sut = ProducerViewModel("test-topic")
        sut.valueProperty.set(null)
        sut.keyProperty.set("test")
        // act
        sut.send()
        // assert
        sut.error.value shouldBe Exception("Invalid value. Value must be not empty")
        sut.canSendProperty.value shouldBe false
    }

    test("Use avro producer if schema registry is configured") {
        // arrange
        configureDi(
            Cluster::class to Cluster.empty().copy(schemaRegistryConfig = SchemaRegistryConfiguration("sample")),
            AvroProducer::class to mockk<Producer>()
        )
        // act
        val sut = ProducerViewModel("test-topic")
        // assert
        sut.producerTypeProperty.value shouldBe "Avro Producer"
    }

    test("send message without key fails") {
        // arrange
        configureDi(
            Cluster::class to Cluster.empty(),
            StringProducer::class to mockk<Producer> {
                every { validate(any(), any()) } returns Unit.right()
            }
        )
        val sut = ProducerViewModel("test-topic")
        sut.valueProperty.set("test")
        sut.keyProperty.set(null)
        // act
        sut.send()
        // assert
        sut.error.value shouldBe Exception("Invalid key. Key must be not empty")
        sut.canSendProperty.value shouldBe false
    }

    test("send happy path") {
        // arrange
        configureDi(
            Cluster::class to Cluster.empty(),
            StringProducer::class to mockk<Producer> {
                every { validate(any(), any()) } returns Unit.right()
                every { send(any(), any(), any()) } returns Unit.right()
            }
        )
        val sut = ProducerViewModel("test-topic")
        sut.valueProperty.set("test")
        sut.keyProperty.set("test")
        // act
        sut.send()
        // assert
        sut.error.value shouldBe null
        sut.canSendProperty.value shouldBe true
    }

    test("show an error if send fails") {
        // arrange
        configureDi(
            Cluster::class to Cluster.empty(),
            StringProducer::class to mockk<Producer> {
                every { validate(any(), any()) } returns Unit.right()
                every { send(any(), any(), any()) } returns Throwable("sample").left()
            }
        )
        val sut = ProducerViewModel("test-topic")
        sut.valueProperty.set("test")
        sut.keyProperty.set("test")
        // act
        sut.send()
        // assert
        sut.error.value shouldNotBe null
        sut.canSendProperty.value shouldBe true
    }

    beforeTest {
        configureFXFramework()
    }
    afterTest {
        cleanupFXFramework()
    }
})
