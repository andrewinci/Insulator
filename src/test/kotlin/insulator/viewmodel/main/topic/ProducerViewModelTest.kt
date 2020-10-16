package insulator.viewmodel.main.topic

import arrow.core.left
import arrow.core.right
import helper.FxContext
import insulator.lib.configuration.model.Cluster
import insulator.lib.configuration.model.SchemaRegistryConfiguration
import insulator.lib.jsonhelper.jsontoavro.JsonToAvroException
import insulator.lib.kafka.AvroProducer
import insulator.lib.kafka.Producer
import insulator.lib.kafka.StringProducer
import io.kotest.assertions.timing.eventually
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

@ExperimentalTime
class ProducerViewModelTest : StringSpec({

    val errorMessage = "Example error"

    "string producer show a validation error if the message is invalid" {
        FxContext().use {
            // arrange
            it.addToDI(
                Cluster::class to Cluster.empty(),
                StringProducer::class to mockk<Producer> {
                    coEvery { validate(any(), any()) } returns JsonToAvroException(errorMessage).left()
                }
            )
            val sut = ProducerViewModel("test-topic")
            // act
            sut.valueProperty.set("test")
            // assert
            eventually(1.seconds) {
                sut.validationErrorProperty.value shouldBe errorMessage
                sut.canSendProperty.value shouldBe false
            }
        }
    }

    "send message without value fails" {
        FxContext().use {
            // arrange
            it.addToDI(
                Cluster::class to Cluster.empty(),
                StringProducer::class to mockk<Producer> {
                    coEvery { validate(any(), any()) } returns Unit.right()
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
    }

    "Use avro producer if schema registry is configured" {
        FxContext().use {
            // arrange
            it.addToDI(
                Cluster::class to Cluster.empty().copy(schemaRegistryConfig = SchemaRegistryConfiguration("sample")),
                AvroProducer::class to mockk<Producer> {
                    coEvery { validate(any(), any()) } returns Unit.right()
                }
            )
            // act
            val sut = ProducerViewModel("test-topic")
            // assert
            sut.producerTypeProperty.value.toString() shouldBe "Avro"
        }
    }

    "send message without key fails" {
        FxContext().use {
            // arrange
            it.addToDI(
                Cluster::class to Cluster.empty(),
                StringProducer::class to mockk<Producer> {
                    coEvery { validate(any(), any()) } returns Unit.right()
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
    }
    "send happy path" {
        FxContext().use {
            // arrange
            it.addToDI(
                Cluster::class to Cluster.empty(),
                StringProducer::class to mockk<Producer> {
                    coEvery { validate(any(), any()) } returns Unit.right()
                    coEvery { send(any(), any(), any()) } returns Unit.right()
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
    }
    "show an error if send fails" {
        FxContext().use {
            // arrange
            it.addToDI(
                Cluster::class to Cluster.empty(),
                StringProducer::class to mockk<Producer> {
                    coEvery { validate(any(), any()) } returns Unit.right()
                    coEvery { send(any(), any(), any()) } returns Throwable("sample").left()
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
    }
})
