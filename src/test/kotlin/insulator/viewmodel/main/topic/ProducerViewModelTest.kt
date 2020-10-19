package insulator.viewmodel.main.topic

import arrow.core.left
import arrow.core.right
import helper.FxContext
import insulator.lib.configuration.model.Cluster
import insulator.lib.configuration.model.SchemaRegistryConfiguration
import insulator.lib.jsonhelper.jsontoavro.JsonToAvroException
import insulator.lib.kafka.AvroProducer
import insulator.lib.kafka.StringProducer
import insulator.lib.kafka.model.Topic
import io.kotest.assertions.timing.eventually
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

@ExperimentalTime
class ProducerViewModelTest : StringSpec({

    val errorMessage = "Example error"

    "string producer show a validation error if the message is invalid" {
        ProducerViewModelTestFixture().use {
            // arrange
            val mockkProducer = mockk<StringProducer> {
                coEvery { validate(any(), any()) } returns JsonToAvroException(errorMessage).left()
            }
            val sut = ProducerViewModel(it.mockTopic, it.cluster, mockk(), mockkProducer)
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
        ProducerViewModelTestFixture().use {
            // arrange
            it.sut.valueProperty.set(null)
            it.sut.keyProperty.set("test")
            // act
            it.sut.send()
            // assert
            it.sut.error.value shouldBe Exception("Invalid value. Value must be not empty")
            it.sut.canSendProperty.value shouldBe false
        }
    }

    "use avro producer if schema registry is configured" {
        ProducerViewModelTestFixture().use {
            // arrange
            val cluster = Cluster.empty().copy(schemaRegistryConfig = SchemaRegistryConfiguration("sample"))
            // act
            val sut = ProducerViewModel(it.mockTopic, cluster, it.avroProducer, mockk())
            // assert
            sut.producerTypeProperty.value.toString() shouldBe "Avro"
        }
    }

    "send message without key fails" {
        ProducerViewModelTestFixture().use {
            // arrange
            it.sut.valueProperty.set("test")
            it.sut.keyProperty.set(null)
            // act
            it.sut.send()
            // assert
            it.sut.error.value shouldBe Exception("Invalid key. Key must be not empty")
            it.sut.canSendProperty.value shouldBe false
        }
    }

    "send happy path" {
        ProducerViewModelTestFixture().use {
            // arrange
            val sut = ProducerViewModel(it.mockTopic, it.cluster, mockk(relaxed = true), it.stringProducer)
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
        ProducerViewModelTestFixture().use {
            // arrange
            val mockProducer = mockk<StringProducer> {
                coEvery { validate(any(), any()) } returns Unit.right()
                coEvery { send(any(), any(), any()) } returns Throwable("sample").left()
            }
            val sut = ProducerViewModel(it.mockTopic, it.cluster, mockk(relaxed = true), mockProducer)
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

class ProducerViewModelTestFixture : FxContext() {
    val mockTopic = Topic.empty()
    val avroProducer =  mockk<AvroProducer> { coEvery { validate(any(), any()) } returns Unit.right() }
    val stringProducer =  mockk<StringProducer> {
        coEvery { validate(any(), any()) } returns Unit.right()
        coEvery { send(any(), any(), any()) } returns Unit.right()
    }
    val sut = ProducerViewModel(
        mockTopic,
        cluster,
        avroProducer,
        stringProducer
    )
}
