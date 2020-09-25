package insulator.viewmodel.main.topic

import arrow.core.left
import helper.cleanupFXFramework
import helper.configureDi
import helper.configureFXFramework
import insulator.lib.configuration.model.Cluster
import insulator.lib.jsonhelper.JsonToAvroException
import insulator.lib.kafka.Producer
import insulator.lib.kafka.StringProducer
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class ProducerViewModelTest : FunSpec({

    val errorMessage = "Example error"

    test("producer show a validation error if the message is invalid") {
        // arrange
        val sut = ProducerViewModel("test-topic")
        // act
        val res = sut.valueProperty.set("test")
        // assert
        sut.validationErrorProperty.value shouldBe errorMessage
    }

    beforeTest {
        configureFXFramework()
        configureDi(
            Cluster::class to Cluster.empty(),
            StringProducer::class to mockk<Producer> {
                every { validate(any(), any()) } returns JsonToAvroException(errorMessage).left()
            }
        )
    }
    afterTest {
        cleanupFXFramework()
    }
})
